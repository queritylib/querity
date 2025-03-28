package io.github.queritylib.querity.spring.data.elasticsearch;

import io.github.queritylib.querity.api.Operator;
import io.github.queritylib.querity.api.SimpleCondition;
import io.github.queritylib.querity.common.util.PropertyUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.core.query.Criteria;

import java.util.EnumMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class ElasticsearchOperatorMapper {
  static final Map<Operator, ElasticSearchOperatorCriteriaProvider> OPERATOR_CRITERIA_MAP = new EnumMap<>(Operator.class);

  static {
    OPERATOR_CRITERIA_MAP.put(Operator.EQUALS, ElasticsearchOperatorMapper::getEquals);
    OPERATOR_CRITERIA_MAP.put(Operator.NOT_EQUALS, (where, value, negate) -> getEquals(where, value, !negate));
    OPERATOR_CRITERIA_MAP.put(Operator.STARTS_WITH, ElasticsearchOperatorMapper::getStartsWith);
    OPERATOR_CRITERIA_MAP.put(Operator.ENDS_WITH, ElasticsearchOperatorMapper::getEndsWith);
    OPERATOR_CRITERIA_MAP.put(Operator.CONTAINS, ElasticsearchOperatorMapper::getContains);
    OPERATOR_CRITERIA_MAP.put(Operator.GREATER_THAN, ElasticsearchOperatorMapper::getGreaterThan);
    OPERATOR_CRITERIA_MAP.put(Operator.GREATER_THAN_EQUALS, ElasticsearchOperatorMapper::getGreaterThanEquals);
    OPERATOR_CRITERIA_MAP.put(Operator.LESSER_THAN, ElasticsearchOperatorMapper::getLesserThan);
    OPERATOR_CRITERIA_MAP.put(Operator.LESSER_THAN_EQUALS, ElasticsearchOperatorMapper::getLesserThanEquals);
    OPERATOR_CRITERIA_MAP.put(Operator.IS_NULL, (where, value, negate) -> getIsNull(where, negate));
    OPERATOR_CRITERIA_MAP.put(Operator.IS_NOT_NULL, (where, value, negate) -> getIsNull(where, !negate));
  }

  private static Criteria getIsNull(Criteria where, boolean negate) {
    return !negate ? where.not().exists() : where.exists();
  }

  private static Criteria getEquals(Criteria where, Object value, boolean negate) {
    return negate ? getNotEquals(where, value) : getEquals(where, value);
  }

  private static Criteria getEquals(Criteria where, Object value) {
    return where.is(value);
  }

  private static Criteria getNotEquals(Criteria where, Object value) {
    return where.not().is(value);
  }

  private static Criteria getStartsWith(Criteria where, Object value, boolean negate) {
    return negate ? where.not().startsWith(value.toString()) : where.startsWith(value.toString());
  }

  private static Criteria getEndsWith(Criteria where, Object value, boolean negate) {
    return negate ? where.not().endsWith(value.toString()) : where.endsWith(value.toString());
  }

  private static Criteria getContains(Criteria where, Object value, boolean negate) {
    return negate ? where.not().contains(value.toString()) : where.contains(value.toString());
  }

  private static Criteria getGreaterThan(Criteria where, Object value, boolean negate) {
    return negate ? where.lessThanEqual(value) : where.greaterThan(value);
  }

  private static Criteria getGreaterThanEquals(Criteria where, Object value, boolean negate) {
    return negate ? where.lessThan(value) : where.greaterThanEqual(value);
  }

  private static Criteria getLesserThan(Criteria where, Object value, boolean negate) {
    return negate ? where.greaterThanEqual(value) : where.lessThan(value);
  }

  private static Criteria getLesserThanEquals(Criteria where, Object value, boolean negate) {
    return negate ? where.greaterThan(value) : where.lessThanEqual(value);
  }

  @FunctionalInterface
  private interface ElasticSearchOperatorCriteriaProvider {
    Criteria getCriteria(Criteria where, Object value, boolean negate);
  }

  public static <T> Criteria getCriteria(Class<T> entityClass, SimpleCondition condition, boolean negate) {
    String propertyPath = condition.getPropertyName();
    Criteria where = Criteria.where(propertyPath);
    Object value = PropertyUtils.getActualPropertyValue(entityClass, propertyPath, condition.getValue());
    return OPERATOR_CRITERIA_MAP.get(condition.getOperator())
        .getCriteria(where, value, negate);
  }
}
