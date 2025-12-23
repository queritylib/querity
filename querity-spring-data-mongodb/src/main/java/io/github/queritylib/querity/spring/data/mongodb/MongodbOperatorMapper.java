package io.github.queritylib.querity.spring.data.mongodb;

import io.github.queritylib.querity.api.FieldReference;
import io.github.queritylib.querity.api.Operator;
import io.github.queritylib.querity.api.PropertyExpression;
import io.github.queritylib.querity.api.PropertyReference;
import io.github.queritylib.querity.api.SimpleCondition;
import io.github.queritylib.querity.common.util.PropertyUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class MongodbOperatorMapper {
  static final Map<Operator, MongodbOperatorCriteriaProvider> OPERATOR_CRITERIA_MAP = new EnumMap<>(Operator.class);
  static final Map<Operator, String> FIELD_TO_FIELD_EXPR_OPERATORS = new EnumMap<>(Operator.class);

  static {
    OPERATOR_CRITERIA_MAP.put(Operator.EQUALS, MongodbOperatorMapper::getEquals);
    OPERATOR_CRITERIA_MAP.put(Operator.NOT_EQUALS, (where, value, negate) -> getEquals(where, value, !negate));
    OPERATOR_CRITERIA_MAP.put(Operator.STARTS_WITH, MongodbOperatorMapper::getStartsWith);
    OPERATOR_CRITERIA_MAP.put(Operator.ENDS_WITH, MongodbOperatorMapper::getEndsWith);
    OPERATOR_CRITERIA_MAP.put(Operator.CONTAINS, MongodbOperatorMapper::getRegex);
    OPERATOR_CRITERIA_MAP.put(Operator.GREATER_THAN, MongodbOperatorMapper::getGreaterThan);
    OPERATOR_CRITERIA_MAP.put(Operator.GREATER_THAN_EQUALS, MongodbOperatorMapper::getGreaterThanEquals);
    OPERATOR_CRITERIA_MAP.put(Operator.LESSER_THAN, MongodbOperatorMapper::getLesserThan);
    OPERATOR_CRITERIA_MAP.put(Operator.LESSER_THAN_EQUALS, MongodbOperatorMapper::getLesserThanEquals);
    OPERATOR_CRITERIA_MAP.put(Operator.IS_NULL, (where, value, negate) -> getIsNull(where, negate));
    OPERATOR_CRITERIA_MAP.put(Operator.IS_NOT_NULL, (where, value, negate) -> getIsNull(where, !negate));
    OPERATOR_CRITERIA_MAP.put(Operator.IN, MongodbOperatorMapper::getIn);
    OPERATOR_CRITERIA_MAP.put(Operator.NOT_IN, MongodbOperatorMapper::getNotIn);

    // Field-to-field comparison operators using MongoDB $expr
    FIELD_TO_FIELD_EXPR_OPERATORS.put(Operator.EQUALS, "$eq");
    FIELD_TO_FIELD_EXPR_OPERATORS.put(Operator.NOT_EQUALS, "$ne");
    FIELD_TO_FIELD_EXPR_OPERATORS.put(Operator.GREATER_THAN, "$gt");
    FIELD_TO_FIELD_EXPR_OPERATORS.put(Operator.GREATER_THAN_EQUALS, "$gte");
    FIELD_TO_FIELD_EXPR_OPERATORS.put(Operator.LESSER_THAN, "$lt");
    FIELD_TO_FIELD_EXPR_OPERATORS.put(Operator.LESSER_THAN_EQUALS, "$lte");
  }

  private static Criteria getIsNull(Criteria where, boolean negate) {
    return getEquals(where, null, negate);
  }

  private static Criteria getEquals(Criteria where, Object value, boolean negate) {
    return negate ? getNotEquals(where, value) : getEquals(where, value);
  }

  private static Criteria getEquals(Criteria where, Object value) {
    return where.is(value);
  }

  private static Criteria getNotEquals(Criteria where, Object value) {
    return where.ne(value);
  }

  private static Criteria getStartsWith(Criteria where, Object value, boolean negate) {
    return getRegex(where, "^" + value, negate);
  }

  private static Criteria getEndsWith(Criteria where, Object value, boolean negate) {
    return getRegex(where, value + "$", negate);
  }

  private static Criteria getRegex(Criteria where, Object value, boolean negate) {
    return negate ?
        where.not().regex(value.toString(), "i") :
        where.regex(value.toString(), "i");
  }

  private static Criteria getGreaterThan(Criteria where, Object value, boolean negate) {
    return negate ? where.lte(value) : where.gt(value);
  }

  private static Criteria getGreaterThanEquals(Criteria where, Object value, boolean negate) {
    return negate ? where.lt(value) : where.gte(value);
  }

  private static Criteria getLesserThan(Criteria where, Object value, boolean negate) {
    return negate ? where.gte(value) : where.lt(value);
  }

  private static Criteria getLesserThanEquals(Criteria where, Object value, boolean negate) {
    return negate ? where.gt(value) : where.lte(value);
  }

  private static Criteria getIn(Criteria where, Object value, boolean negate) {
    if (value.getClass().isArray()) {
      return negate ? where.nin((Object[]) value) : where.in((Object[]) value);
    } else {
      throw new IllegalArgumentException("Value must be an array");
    }
  }

  private static Criteria getNotIn(Criteria where, Object value, boolean negate) {
    if (value.getClass().isArray()) {
      return negate ? where.in((Object[]) value) : where.nin((Object[]) value);
    } else {
      throw new IllegalArgumentException("Value must be an array");
    }
  }

  @FunctionalInterface
  private interface MongodbOperatorCriteriaProvider {
    Criteria getCriteria(Criteria where, Object value, boolean negate);
  }

  public static <T> Criteria getCriteria(Class<T> entityClass, SimpleCondition condition, boolean negate) {
    // Check if we have a function expression on the left side
    if (condition.hasLeftExpression()) {
      PropertyExpression leftExpr = condition.getLeftExpression();
      if (MongodbFunctionMapper.containsFunction(leftExpr)) {
        return getFunctionExpressionCriteria(condition, negate);
      }
      // If it's just a PropertyReference, continue with normal processing
      if (leftExpr instanceof PropertyReference pr) {
        return getCriteriaForProperty(entityClass, pr.getPropertyName(), condition, negate);
      }
    }

    String propertyPath = condition.getPropertyName();
    return getCriteriaForProperty(entityClass, propertyPath, condition, negate);
  }

  private static <T> Criteria getCriteriaForProperty(Class<T> entityClass, String propertyPath, SimpleCondition condition, boolean negate) {
    if (condition.isFieldReference()) {
      return getFieldToFieldCriteria(condition, negate);
    }

    Criteria where = Criteria.where(propertyPath);
    Object value = PropertyUtils.getActualPropertyValue(entityClass, propertyPath, condition.getValue());
    return OPERATOR_CRITERIA_MAP.get(condition.getOperator())
        .getCriteria(where, value, negate);
  }

  private static Criteria getFunctionExpressionCriteria(SimpleCondition condition, boolean negate) {
    // For function expressions, we need to use $expr with aggregation operators
    Object leftExpr = MongodbFunctionMapper.toExpression(condition.getEffectiveLeftExpression());
    Object value = condition.getValue();
    Operator operator = condition.getOperator();

    if (negate) {
      operator = getNegatedOperator(operator);
    }

    // Build the comparison expression
    Document comparisonExpr = buildComparisonExpression(leftExpr, value, operator);
    return new Criteria("$expr").is(comparisonExpr);
  }

  private static Document buildComparisonExpression(Object leftExpr, Object value, Operator operator) {
    String mongoOp = switch (operator) {
      case EQUALS -> "$eq";
      case NOT_EQUALS -> "$ne";
      case GREATER_THAN -> "$gt";
      case GREATER_THAN_EQUALS -> "$gte";
      case LESSER_THAN -> "$lt";
      case LESSER_THAN_EQUALS -> "$lte";
      case IS_NULL -> "$eq";
      case IS_NOT_NULL -> "$ne";
      default -> throw new UnsupportedOperationException(
          "Operator " + operator + " is not supported with function expressions in MongoDB");
    };

    Object rightValue;
    if (operator == Operator.IS_NULL || operator == Operator.IS_NOT_NULL) {
      rightValue = null;
    } else {
      rightValue = value;
    }

    return new Document(mongoOp, List.of(leftExpr, rightValue));
  }

  private static Criteria getFieldToFieldCriteria(SimpleCondition condition, boolean negate) {
    String leftField = "$" + condition.getPropertyName();
    FieldReference fieldRef = condition.getFieldReference();
    String rightField = "$" + fieldRef.getFieldName();

    Operator operator = condition.getOperator();
    if (negate) {
      operator = getNegatedOperator(operator);
    }

    String mongoOp = FIELD_TO_FIELD_EXPR_OPERATORS.get(operator);
    Document exprDoc = new Document(mongoOp, List.of(leftField, rightField));
    return new Criteria("$expr").is(exprDoc);
  }

  private static Operator getNegatedOperator(Operator operator) {
    return switch (operator) {
      case EQUALS -> Operator.NOT_EQUALS;
      case NOT_EQUALS -> Operator.EQUALS;
      case GREATER_THAN -> Operator.LESSER_THAN_EQUALS;
      case GREATER_THAN_EQUALS -> Operator.LESSER_THAN;
      case LESSER_THAN -> Operator.GREATER_THAN_EQUALS;
      case LESSER_THAN_EQUALS -> Operator.GREATER_THAN;
      default -> throw new IllegalArgumentException("Operator " + operator + " does not support field-to-field comparison");
    };
  }
}
