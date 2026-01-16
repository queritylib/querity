package io.github.queritylib.querity.parser;

import io.github.queritylib.querity.api.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class QueryVisitor extends QueryParserBaseVisitor<Object> {

  @Override
  public QueryDefinition visitQuery(QueryParser.QueryContext ctx) {
    boolean distinct = ctx.DISTINCT() != null;
    Select select = ctx.selectClause() != null ? (Select) visit(ctx.selectClause()) : null;
    Condition filter = ctx.condition() != null ? (Condition) visit(ctx.condition()) : null;
    GroupBy groupBy = ctx.groupByClause() != null ? (GroupBy) visit(ctx.groupByClause()) : null;
    Condition having = ctx.havingClause() != null ? (Condition) visit(ctx.havingClause()) : null;
    Sort[] sorts = ctx.SORT() != null ? (Sort[]) visit(ctx.sortFields()) : new Sort[0];
    Pagination pagination = ctx.PAGINATION() != null ? (Pagination) visit(ctx.paginationParams()) : null;

    // If query has projection features (select, groupBy, having), return AdvancedQuery
    if (select != null || groupBy != null || having != null) {
      return Querity.advancedQuery()
          .distinct(distinct)
          .select(select)
          .filter(filter)
          .groupBy(groupBy)
          .having(having)
          .pagination(pagination)
          .sort(sorts)
          .build();
    }

    // Otherwise return simple Query
    return Querity.query()
        .distinct(distinct)
        .filter(filter)
        .pagination(pagination)
        .sort(sorts)
        .build();
  }

  @Override
  public Object visitSelectClause(QueryParser.SelectClauseContext ctx) {
    return visit(ctx.selectFields());
  }

  @Override
  public Object visitSelectFields(QueryParser.SelectFieldsContext ctx) {
    List<PropertyExpression> expressions = new ArrayList<>();
    for (QueryParser.SelectFieldContext fieldCtx : ctx.selectField()) {
      PropertyExpression expr = (PropertyExpression) visit(fieldCtx);
      expressions.add(expr);
    }

    // Check if all expressions are simple PropertyReferences (no functions or aliases)
    boolean allSimpleProperties = expressions.stream()
        .allMatch(e -> e instanceof PropertyReference pr && !pr.hasAlias());

    if (allSimpleProperties) {
      // Use propertyNames for backward compatibility
      return SimpleSelect.of(extractPropertyNames(expressions));
    } else {
      // Use expressions when functions are involved
      return SimpleSelect.ofExpressions(expressions.toArray(new PropertyExpression[0]));
    }
  }

  @Override
  public Object visitSelectField(QueryParser.SelectFieldContext ctx) {
    PropertyExpression expr = (PropertyExpression) visit(ctx.propertyExpression());
    // Handle alias if present
    if (ctx.AS() != null && ctx.alias != null) {
      String alias = (String) visit(ctx.alias);
      if (expr instanceof FunctionCall fc) {
        return fc.as(alias);
      }
      if (expr instanceof PropertyReference pr) {
        return pr.as(alias);
      }
    }
    return expr;
  }

  @Override
  public Object visitGroupByClause(QueryParser.GroupByClauseContext ctx) {
    return visit(ctx.groupByFields());
  }

  @Override
  public Object visitGroupByFields(QueryParser.GroupByFieldsContext ctx) {
    List<PropertyExpression> expressions = new ArrayList<>();
    for (QueryParser.PropertyExpressionContext exprCtx : ctx.propertyExpression()) {
      PropertyExpression expr = (PropertyExpression) visit(exprCtx);
      expressions.add(expr);
    }

    // Check if all expressions are simple PropertyReferences (no functions)
    boolean allSimpleProperties = expressions.stream()
        .allMatch(e -> e instanceof PropertyReference);

    if (allSimpleProperties) {
      // Use propertyNames for backward compatibility
      return SimpleGroupBy.of(extractPropertyNames(expressions));
    } else {
      // Use expressions when functions are involved
      return SimpleGroupBy.ofExpressions(expressions.toArray(new PropertyExpression[0]));
    }
  }

  @Override
  public Object visitHavingClause(QueryParser.HavingClauseContext ctx) {
    return visit(ctx.condition());
  }

  @Override
  public Object visitPropertyExpression(QueryParser.PropertyExpressionContext ctx) {
    if (ctx.propertyName() != null) {
      return PropertyReference.of((String) visit(ctx.propertyName()));
    } else {
      return visit(ctx.functionCall());
    }
  }

  @Override
  public String visitPropertyName(QueryParser.PropertyNameContext ctx) {
    if (ctx.PROPERTY() != null) {
      return ctx.PROPERTY().getText();
    }
    return unescapeBacktickProperty(ctx.BACKTICK_PROPERTY().getText());
  }

  @Override
  public Object visitFunctionCall(QueryParser.FunctionCallContext ctx) {
    if (ctx.nullaryFunction() != null) {
      return visit(ctx.nullaryFunction());
    }

    Function function = (Function) visit(ctx.functionName());
    List<FunctionArgument> arguments = new ArrayList<>();

    if (ctx.functionArgs() != null) {
      for (QueryParser.FunctionArgContext argCtx : ctx.functionArgs().functionArg()) {
        arguments.add((FunctionArgument) visit(argCtx));
      }
    }

    return FunctionCall.builder()
        .function(function)
        .arguments(arguments)
        .build();
  }

  @Override
  public Object visitNullaryFunction(QueryParser.NullaryFunctionContext ctx) {
    Function function;
    if (ctx.CURRENT_DATE_FUNC() != null) {
      function = Function.CURRENT_DATE;
    } else if (ctx.CURRENT_TIME_FUNC() != null) {
      function = Function.CURRENT_TIME;
    } else {
      function = Function.CURRENT_TIMESTAMP;
    }
    return FunctionCall.of(function);
  }

  @Override
  public Object visitFunctionName(QueryParser.FunctionNameContext ctx) {
    String functionName = ctx.getText();
    try {
      return Function.valueOf(functionName.toUpperCase(Locale.ROOT));
    } catch (IllegalArgumentException ex) {
      throw new UnsupportedOperationException("Unsupported function: " + functionName, ex);
    }
  }

  @Override
  public FunctionArgument visitFunctionArg(QueryParser.FunctionArgContext ctx) {
    if (ctx.propertyExpression() != null) {
      return (PropertyExpression) visit(ctx.propertyExpression());
    } else {
      // Wrap raw values in Literal for type safety
      Object value = visit(ctx.simpleValue());
      return Literal.of(value);
    }
  }

  @Override
  public Object visitPaginationParams(QueryParser.PaginationParamsContext ctx) {
    return Pagination.builder()
        .page(Integer.parseInt(ctx.INT_VALUE(0).getText()))
        .pageSize(Integer.parseInt(ctx.INT_VALUE(1).getText()))
        .build();
  }

  @Override
  public Object visitCondition(QueryParser.ConditionContext ctx) {
    if (ctx.conditionWrapper() != null) {
      return visit(ctx.conditionWrapper());
    } else if (ctx.notCondition() != null) {
      return visit(ctx.notCondition());
    } else {
      return visit(ctx.simpleCondition());
    }
  }

  @Override
  public Condition visitConditionWrapper(QueryParser.ConditionWrapperContext ctx) {
    List<Condition> conditions = ctx.condition().stream()
        .map(this::visit)
        .map(Condition.class::cast)
        .toList();

    if (ctx.AND() != null) {
      return AndConditionsWrapper.builder()
          .conditions(conditions)
          .build();
    } else {
      return OrConditionsWrapper.builder()
          .conditions(conditions)
          .build();
    }
  }

  @Override
  public Object visitNotCondition(QueryParser.NotConditionContext ctx) {
    Condition condition = (Condition) visit(ctx.condition());
    return NotCondition.builder()
        .condition(condition)
        .build();
  }

  @Override
  public Condition visitSimpleCondition(QueryParser.SimpleConditionContext ctx) {
    PropertyExpression leftExpr = (PropertyExpression) visit(ctx.propertyExpression());
    Operator operator = (Operator) visit(ctx.operator());
    Object value = null;

    if (operator.getRequiredValuesCount() > 0) {
      if (ctx.arrayValue() != null) {
        value = visitArrayValue(ctx.arrayValue());
      } else if (ctx.simpleValue() != null) {
        value = visitSimpleValue(ctx.simpleValue());
      } else if (ctx.valueProperty != null) {
        // Use PROPERTY as field reference
        String fieldName = (String) visit(ctx.valueProperty);
        value = FieldReference.of(fieldName);
      }
    }

    // Determine if we should use propertyName or leftExpression
    if (leftExpr instanceof PropertyReference pr) {
      return SimpleCondition.builder()
          .propertyName(pr.getPropertyName())
          .operator(operator)
          .value(value)
          .build();
    } else {
      return SimpleCondition.builder()
          .leftExpression(leftExpr)
          .operator(operator)
          .value(value)
          .build();
    }
  }

  @Override
  public Object visitSimpleValue(QueryParser.SimpleValueContext ctx) {
    Object value;
    if (ctx.INT_VALUE() != null) {
      value = Integer.parseInt(ctx.INT_VALUE().getText());
    } else if (ctx.DECIMAL_VALUE() != null) {
      value = new BigDecimal(ctx.DECIMAL_VALUE().getText());
    } else if (ctx.BOOLEAN_VALUE() != null) {
      value = Boolean.valueOf(ctx.BOOLEAN_VALUE().getText().toLowerCase());
    } else {
      value = ctx.STRING_VALUE().getText().replace("\"", "");  // Remove quotes if present
    }
    return value;
  }

  @Override
  public Object visitArrayValue(QueryParser.ArrayValueContext ctx) {
    return ctx.simpleValue().stream()
        .map(this::visitSimpleValue)
        .toArray();
  }

  @Override
  public Object visitOperator(QueryParser.OperatorContext ctx) {
    if (ctx.GTE() != null) {
      return Operator.GREATER_THAN_EQUALS;
    } else if (ctx.LTE() != null) {
      return Operator.LESSER_THAN_EQUALS;
    } else if (ctx.GT() != null) {
      return Operator.GREATER_THAN;
    } else if (ctx.LT() != null) {
      return Operator.LESSER_THAN;
    } else if (ctx.EQ() != null) {
      return Operator.EQUALS;
    } else if (ctx.NEQ() != null) {
      return Operator.NOT_EQUALS;
    } else if (ctx.STARTS_WITH() != null) {
      return Operator.STARTS_WITH;
    } else if (ctx.ENDS_WITH() != null) {
      return Operator.ENDS_WITH;
    } else if (ctx.CONTAINS() != null) {
      return Operator.CONTAINS;
    } else if (ctx.IS_NULL() != null) {
      return Operator.IS_NULL;
    } else if (ctx.IS_NOT_NULL() != null) {
      return Operator.IS_NOT_NULL;
    } else if (ctx.IN() != null) {
      return Operator.IN;
    } else if (ctx.NOT_IN() != null) {
      return Operator.NOT_IN;
    } else {
      throw new UnsupportedOperationException("Unsupported operator: " + ctx.getText());
    }
  }

  @Override
  public Object visitSortFields(QueryParser.SortFieldsContext ctx) {
    return ctx.sortField().stream()
        .map(this::visit)
        .map(Sort.class::cast)
        .toArray(Sort[]::new);
  }

  @Override
  public Object visitSortField(QueryParser.SortFieldContext ctx) {
    PropertyExpression expr = (PropertyExpression) visit(ctx.propertyExpression());
    SimpleSort.Direction direction = ctx.direction() != null ?
        (SimpleSort.Direction) visit(ctx.direction()) : SimpleSort.Direction.ASC;

    if (expr instanceof PropertyReference pr) {
      return Querity.sortBy(pr.getPropertyName(), direction);
    } else {
      return Querity.sortBy(expr, direction);
    }
  }

  @Override
  public Object visitDirection(QueryParser.DirectionContext ctx) {
    if (ctx.ASC() != null) {
      return SimpleSort.Direction.ASC;
    } else {
      return SimpleSort.Direction.DESC;
    }
  }

  private static String unescapeBacktickProperty(String text) {
    String raw = text.substring(1, text.length() - 1);
    StringBuilder result = new StringBuilder(raw.length());
    boolean escaped = false;
    for (int i = 0; i < raw.length(); i++) {
      char current = raw.charAt(i);
      if (escaped) {
        result.append(current);
        escaped = false;
      } else if (current == '\\' && i + 1 < raw.length()) {
        escaped = true;
      } else {
        result.append(current);
      }
    }
    return result.toString();
  }

  /**
   * Extracts property names from a list of PropertyExpressions.
   * <p>Assumes all expressions are PropertyReference instances.
   *
   * @param expressions list of PropertyExpression (must all be PropertyReference)
   * @return array of property names
   */
  private static String[] extractPropertyNames(List<PropertyExpression> expressions) {
    return expressions.stream()
        .map(PropertyReference.class::cast)
        .map(PropertyReference::getPropertyName)
        .toArray(String[]::new);
  }
}
