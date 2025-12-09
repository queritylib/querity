package io.github.queritylib.querity.jpa;

import io.github.queritylib.querity.api.FieldReference;
import io.github.queritylib.querity.api.Operator;
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
    OPERATOR_PREDICATE_MAP.put(Operator.IS_NULL, (path, value, cb) -> getIsNull(path, cb));
    OPERATOR_PREDICATE_MAP.put(Operator.IS_NOT_NULL, (path, value, cb) -> getIsNotNull(path, cb));
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

  private static Predicate getIsNull(Path<?> path, CriteriaBuilder cb) {
    return cb.isNull(path);
  }

  private static Predicate getIsNotNull(Path<?> path, CriteriaBuilder cb) {
    return cb.isNotNull(path);
  }

  private static Predicate getNotEquals(Path<?> path, Object value, CriteriaBuilder cb) {
    return cb.or(cb.notEqual(path, value), getIsNull(path, cb));
  }

  private static Predicate getEquals(Path<?> path, Object value, CriteriaBuilder cb) {
    return cb.and(cb.equal(path, value), getIsNotNull(path, cb));
  }

  private static Predicate getStartsWith(Path<?> path, Object value, CriteriaBuilder cb) {
    return getLike(path, value.toString() + "%", cb);
  }

  private static Predicate getEndsWith(Path<?> path, Object value, CriteriaBuilder cb) {
    return getLike(path, "%" + value.toString(), cb);
  }

  private static Predicate getContains(Path<?> path, Object value, CriteriaBuilder cb) {
    return getLike(path, "%" + value.toString() + "%", cb);
  }

  private static Predicate getLike(Path<?> path, Object value, CriteriaBuilder cb) {
    return cb.and(cb.like(cb.lower(path.as(String.class)), value.toString().toLowerCase()), getIsNotNull(path, cb));
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private static Predicate getGreaterThan(Path<?> path, Object value, CriteriaBuilder cb) {
    return cb.greaterThan((Expression) path, (Comparable) value);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private static Predicate getGreaterThanEquals(Path<?> path, Object value, CriteriaBuilder cb) {
    return cb.greaterThanOrEqualTo((Expression) path, (Comparable) value);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private static Predicate getLesserThan(Path<?> path, Object value, CriteriaBuilder cb) {
    return cb.lessThan((Expression) path, (Comparable) value);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private static Predicate getLesserThanEquals(Path<?> path, Object value, CriteriaBuilder cb) {
    return cb.lessThanOrEqualTo((Expression) path, (Comparable) value);
  }

  private static Predicate getIn(Path<?> path, Object value, CriteriaBuilder cb) {
    if (value.getClass().isArray()) {
      return cb.and(path.in((Object[]) value), getIsNotNull(path, cb));
    } else {
      throw new IllegalArgumentException("Value must be an array");
    }
  }

  private static Predicate getNotIn(Path<?> path, Object value, CriteriaBuilder cb) {
    return getIn(path, value, cb).not();
  }

  // Field-to-field comparison methods
  private static Predicate getFieldEquals(Path<?> leftPath, Path<?> rightPath, CriteriaBuilder cb) {
    return cb.and(cb.equal(leftPath, rightPath), getIsNotNull(leftPath, cb), getIsNotNull(rightPath, cb));
  }

  private static Predicate getFieldNotEquals(Path<?> leftPath, Path<?> rightPath, CriteriaBuilder cb) {
    return cb.or(cb.notEqual(leftPath, rightPath), getIsNull(leftPath, cb), getIsNull(rightPath, cb));
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private static Predicate getFieldGreaterThan(Path<?> leftPath, Path<?> rightPath, CriteriaBuilder cb) {
    return cb.greaterThan((Expression) leftPath, (Expression) rightPath);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private static Predicate getFieldGreaterThanEquals(Path<?> leftPath, Path<?> rightPath, CriteriaBuilder cb) {
    return cb.greaterThanOrEqualTo((Expression) leftPath, (Expression) rightPath);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private static Predicate getFieldLesserThan(Path<?> leftPath, Path<?> rightPath, CriteriaBuilder cb) {
    return cb.lessThan((Expression) leftPath, (Expression) rightPath);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private static Predicate getFieldLesserThanEquals(Path<?> leftPath, Path<?> rightPath, CriteriaBuilder cb) {
    return cb.lessThanOrEqualTo((Expression) leftPath, (Expression) rightPath);
  }

  @FunctionalInterface
  private interface JpaOperatorPredicateProvider {
    Predicate getPredicate(Path<?> path, Object value, CriteriaBuilder cb);
  }

  @FunctionalInterface
  private interface JpaFieldToFieldPredicateProvider {
    Predicate getPredicate(Path<?> leftPath, Path<?> rightPath, CriteriaBuilder cb);
  }

  public static <T> Predicate getPredicate(Class<T> entityClass, SimpleCondition condition, Metamodel metamodel, Root<?> root, CriteriaBuilder cb) {
    String propertyPath = condition.getPropertyName();
    Path<?> leftPath = JpaPropertyUtils.getPath(root, propertyPath, metamodel);

    if (condition.isFieldReference()) {
      FieldReference fieldRef = condition.getFieldReference();
      Path<?> rightPath = JpaPropertyUtils.getPath(root, fieldRef.getFieldName(), metamodel);
      return FIELD_TO_FIELD_PREDICATE_MAP.get(condition.getOperator())
          .getPredicate(leftPath, rightPath, cb);
    }

    Object value = PropertyUtils.getActualPropertyValue(entityClass, propertyPath, condition.getValue());
    return OPERATOR_PREDICATE_MAP.get(condition.getOperator())
        .getPredicate(leftPath, value, cb);
  }
}
