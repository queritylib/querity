package io.github.queritylib.querity.parser;

import io.github.queritylib.querity.api.*;

import java.math.BigDecimal;
import java.util.List;

class QueryVisitor extends QueryParserBaseVisitor<Object> {

  @Override
  public Query visitQuery(QueryParser.QueryContext ctx) {
    boolean distinct = ctx.DISTINCT() != null;
    Condition filter = ctx.condition() != null ? (Condition) visit(ctx.condition()) : null;
    Sort[] sorts = ctx.SORT() != null ? (Sort[]) visit(ctx.sortFields()) : new Sort[0];
    Pagination pagination = ctx.PAGINATION() != null ? (Pagination) visit(ctx.paginationParams()) : null;

    return Querity.query()
        .distinct(distinct)
        .filter(filter)
        .pagination(pagination)
        .sort(sorts)
        .build();
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
    String propertyName = ctx.PROPERTY().getText();
    Operator operator = (Operator) visit(ctx.operator());
    Object value = null;
    if (operator.getRequiredValuesCount() > 0) {
      if (ctx.INT_VALUE() != null) {
        value = Integer.parseInt(ctx.INT_VALUE().getText());
      } else if (ctx.DECIMAL_VALUE() != null) {
        value = new BigDecimal(ctx.DECIMAL_VALUE().getText());
      } else if (ctx.BOOLEAN_VALUE() != null) {
        value = Boolean.valueOf(ctx.BOOLEAN_VALUE().getText());
      } else {
        value = ctx.STRING_VALUE().getText().replace("\"", "");  // Remove quotes if present
      }
    }

    return SimpleCondition.builder()
        .propertyName(propertyName)
        .operator(operator)
        .value(value)
        .build();
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
    return Querity.sortBy(
        ctx.PROPERTY().getText(),
        ctx.direction() != null ? (Sort.Direction) visit(ctx.direction()) : Sort.Direction.ASC);
  }

  @Override
  public Object visitDirection(QueryParser.DirectionContext ctx) {
    if (ctx.ASC() != null) {
      return Sort.Direction.ASC;
    } else {
      return Sort.Direction.DESC;
    }
  }
}

