package io.github.queritylib.querity.jpa;

import io.github.queritylib.querity.api.FieldReference;
import io.github.queritylib.querity.api.Operator;
import io.github.queritylib.querity.api.PropertyExpression;
import io.github.queritylib.querity.api.PropertyReference;
import io.github.queritylib.querity.api.SimpleCondition;
import io.github.queritylib.querity.common.util.PropertyUtils;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.Metamodel;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.EnumMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class JpaOperatorMapper {
  static final Map<Operator, JpaOperatorPredicateProvider> OPERATOR_PREDICATE_MAP = new EnumMap<>(Operator.class);
  static final Map<Operator, JpaFieldToFieldPredicateProvider> FIELD_TO_FIELD_PREDICATE_MAP = new EnumMap<>(Operator.class);

  static {
    OPERATOR_PREDICATE_MAP.put(Operator.EQUALS, JpaOperatorMapper::getEquals);
    OPERATOR_PREDICATE_MAP.put(Operator.NOT_EQUALS, JpaOperatorMapper::getNotEquals);
    OPERATOR_PREDICATE_MAP.put(Operator.STARTS_WITH, JpaOperatorMapper::getStartsWith);
    OPERATOR_PREDICATE_MAP.put(Operator.ENDS_WITH, JpaOperatorMapper::getEndsWith);
    OPERATOR_PREDICATE_MAP.put(Operator.CONTAINS, JpaOperatorMapper::getContains);
    OPERATOR_PREDICATE_MAP.put(Operator.GREATER_THAN, JpaOperatorMapper::getGreaterThan);
    OPERATOR_PREDICATE_MAP.put(Operator.GREATER_THAN_EQUALS, JpaOperatorMapper::getGreaterThanEquals);
    OPERATOR_PREDICATE_MAP.put(Operator.LESSER_THAN, JpaOperatorMapper::getLesserThan);
    OPERATOR_PREDICATE_MAP.put(Operator.LESSER_THAN_EQUALS, JpaOperatorMapper::getLesserThanEquals);
    OPERATOR_PREDICATE_MAP.put(Operator.IS_NULL, (expr, value, cb) -> getIsNull(expr, cb));
    OPERATOR_PREDICATE_MAP.put(Operator.IS_NOT_NULL, (expr, value, cb) -> getIsNotNull(expr, cb));
    OPERATOR_PREDICATE_MAP.put(Operator.IN, JpaOperatorMapper::getIn);
    OPERATOR_PREDICATE_MAP.put(Operator.NOT_IN, JpaOperatorMapper::getNotIn);

    // Field-to-field comparison operators
    FIELD_TO_FIELD_PREDICATE_MAP.put(Operator.EQUALS, JpaOperatorMapper::getFieldEquals);
    FIELD_TO_FIELD_PREDICATE_MAP.put(Operator.NOT_EQUALS, JpaOperatorMapper::getFieldNotEquals);
    FIELD_TO_FIELD_PREDICATE_MAP.put(Operator.GREATER_THAN, JpaOperatorMapper::getFieldGreaterThan);
    FIELD_TO_FIELD_PREDICATE_MAP.put(Operator.GREATER_THAN_EQUALS, JpaOperatorMapper::getFieldGreaterThanEquals);
    FIELD_TO_FIELD_PREDICATE_MAP.put(Operator.LESSER_THAN, JpaOperatorMapper::getFieldLesserThan);
    FIELD_TO_FIELD_PREDICATE_MAP.put(Operator.LESSER_THAN_EQUALS, JpaOperatorMapper::getFieldLesserThanEquals);
  }

  private static Predicate getIsNull(Expression<?> expr, CriteriaBuilder cb) {
    return cb.isNull(expr);
  }

  private static Predicate getIsNotNull(Expression<?> expr, CriteriaBuilder cb) {
    return cb.isNotNull(expr);
  }

  private static Predicate getNotEquals(Expression<?> expr, Object value, CriteriaBuilder cb) {
    return cb.or(cb.notEqual(expr, value), getIsNull(expr, cb));
  }

  private static Predicate getEquals(Expression<?> expr, Object value, CriteriaBuilder cb) {
    return cb.and(cb.equal(expr, value), getIsNotNull(expr, cb));
  }

  @SuppressWarnings("unchecked")
  private static Predicate getStartsWith(Expression<?> expr, Object value, CriteriaBuilder cb) {
    return getLike((Expression<String>) expr, value.toString() + "%", cb);
  }

  @SuppressWarnings("unchecked")
  private static Predicate getEndsWith(Expression<?> expr, Object value, CriteriaBuilder cb) {
    return getLike((Expression<String>) expr, "%" + value.toString(), cb);
  }

  @SuppressWarnings("unchecked")
  private static Predicate getContains(Expression<?> expr, Object value, CriteriaBuilder cb) {
    return getLike((Expression<String>) expr, "%" + value.toString() + "%", cb);
  }

  private static Predicate getLike(Expression<String> expr, Object value, CriteriaBuilder cb) {
    return cb.and(cb.like(cb.lower(expr), value.toString().toLowerCase()), getIsNotNull(expr, cb));
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private static Predicate getGreaterThan(Expression<?> expr, Object value, CriteriaBuilder cb) {
    return cb.greaterThan((Expression) expr, (Comparable) value);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private static Predicate getGreaterThanEquals(Expression<?> expr, Object value, CriteriaBuilder cb) {
    return cb.greaterThanOrEqualTo((Expression) expr, (Comparable) value);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private static Predicate getLesserThan(Expression<?> expr, Object value, CriteriaBuilder cb) {
    return cb.lessThan((Expression) expr, (Comparable) value);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private static Predicate getLesserThanEquals(Expression<?> expr, Object value, CriteriaBuilder cb) {
    return cb.lessThanOrEqualTo((Expression) expr, (Comparable) value);
  }

  private static Predicate getIn(Expression<?> expr, Object value, CriteriaBuilder cb) {
    if (value.getClass().isArray()) {
      return cb.and(expr.in((Object[]) value), getIsNotNull(expr, cb));
    } else {
      throw new IllegalArgumentException("Value must be an array");
    }
  }

  private static Predicate getNotIn(Expression<?> expr, Object value, CriteriaBuilder cb) {
    return getIn(expr, value, cb).not();
  }

  // Field-to-field comparison methods
  private static Predicate getFieldEquals(Expression<?> leftExpr, Expression<?> rightExpr, CriteriaBuilder cb) {
    return cb.and(cb.equal(leftExpr, rightExpr), getIsNotNull(leftExpr, cb), getIsNotNull(rightExpr, cb));
  }

  private static Predicate getFieldNotEquals(Expression<?> leftExpr, Expression<?> rightExpr, CriteriaBuilder cb) {
    return cb.or(cb.notEqual(leftExpr, rightExpr), getIsNull(leftExpr, cb), getIsNull(rightExpr, cb));
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private static Predicate getFieldGreaterThan(Expression<?> leftExpr, Expression<?> rightExpr, CriteriaBuilder cb) {
    return cb.greaterThan((Expression) leftExpr, (Expression) rightExpr);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private static Predicate getFieldGreaterThanEquals(Expression<?> leftExpr, Expression<?> rightExpr, CriteriaBuilder cb) {
    return cb.greaterThanOrEqualTo((Expression) leftExpr, (Expression) rightExpr);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private static Predicate getFieldLesserThan(Expression<?> leftExpr, Expression<?> rightExpr, CriteriaBuilder cb) {
    return cb.lessThan((Expression) leftExpr, (Expression) rightExpr);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private static Predicate getFieldLesserThanEquals(Expression<?> leftExpr, Expression<?> rightExpr, CriteriaBuilder cb) {
    return cb.lessThanOrEqualTo((Expression) leftExpr, (Expression) rightExpr);
  }

  @FunctionalInterface
  private interface JpaOperatorPredicateProvider {
    Predicate getPredicate(Expression<?> expr, Object value, CriteriaBuilder cb);
  }

  @FunctionalInterface
  private interface JpaFieldToFieldPredicateProvider {
    Predicate getPredicate(Expression<?> leftExpr, Expression<?> rightExpr, CriteriaBuilder cb);
  }

  public static <T> Predicate getPredicate(Class<T> entityClass, SimpleCondition condition, Metamodel metamodel, Root<?> root, CriteriaBuilder cb) {
    // Get the left side expression (either from leftExpression or propertyName)
    Expression<?> leftExpression;
    String propertyPath;

    if (condition.hasLeftExpression()) {
      PropertyExpression leftExpr = condition.getLeftExpression();
      leftExpression = JpaFunctionMapper.toExpression(leftExpr, root, cb, metamodel);
      // For value conversion, try to get propertyPath from PropertyReference
      if (leftExpr instanceof PropertyReference pr) {
        propertyPath = pr.getPropertyName();
      } else {
        propertyPath = null;
      }
    } else {
      propertyPath = condition.getPropertyName();
      leftExpression = JpaPropertyUtils.getPath(root, propertyPath, metamodel);
    }

    if (condition.isFieldReference()) {
      FieldReference fieldRef = condition.getFieldReference();
      Expression<?> rightExpression = JpaPropertyUtils.getPath(root, fieldRef.getFieldName(), metamodel);
      return FIELD_TO_FIELD_PREDICATE_MAP.get(condition.getOperator())
          .getPredicate(leftExpression, rightExpression, cb);
    }

    Object value;
    if (propertyPath != null) {
      value = PropertyUtils.getActualPropertyValue(entityClass, propertyPath, condition.getValue());
    } else {
      // For function expressions, use the raw value
      value = condition.getValue();
    }
    return OPERATOR_PREDICATE_MAP.get(condition.getOperator())
        .getPredicate(leftExpression, value, cb);
  }
}
