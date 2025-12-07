package io.github.queritylib.querity.api;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public interface Querity {
  <T> List<T> findAll(Class<T> entityClass, Query query);

  <T> Long count(Class<T> entityClass, Condition condition);

  /**
   * Execute a projection query returning a list of maps with the selected properties.
   *
   * @param entityClass the entity class to query
   * @param query the query with select clause
   * @return a list of maps containing the selected properties
   */
  default List<Map<String, Object>> findAllProjected(Class<?> entityClass, Query query) {
    throw new UnsupportedOperationException("Projection queries are not supported by this implementation");
  }

  static Query.QueryBuilder query() {
    return Query.builder();
  }

  /**
   * Filter by propertyName EQUALS value
   *
   * @param propertyName the property name
   * @param value        the value
   * @return a SimpleCondition with {@link Operator#EQUALS}
   */
  static SimpleCondition filterBy(String propertyName, Object value) {
    return SimpleCondition.builder()
        .propertyName(propertyName).value(value).build();
  }

  static SimpleCondition filterBy(String propertyName, Operator operator, Object value) {
    return SimpleCondition.builder()
        .propertyName(propertyName).operator(operator).value(value).build();
  }

  static SimpleCondition filterBy(String propertyName, Operator operator) {
    return SimpleCondition.builder()
        .propertyName(propertyName).operator(operator).build();
  }

  static <T> NativeConditionWrapper<T> filterByNative(T nativeCondition) {
    return NativeConditionWrapper.<T>builder()
        .nativeCondition(nativeCondition)
        .build();
  }

  static AndConditionsWrapper and(Condition... conditions) {
    return AndConditionsWrapper.builder().conditions(Arrays.asList(conditions)).build();
  }

  static OrConditionsWrapper or(Condition... conditions) {
    return OrConditionsWrapper.builder().conditions(Arrays.asList(conditions)).build();
  }

  static NotCondition not(Condition condition) {
    return NotCondition.builder().condition(condition).build();
  }

  static Pagination paged(Integer page, Integer pageSize) {
    return Pagination.builder().page(page).pageSize(pageSize).build();
  }

  static SimpleSort sortBy(String propertyName) {
    return sortBy(propertyName, SimpleSort.Direction.ASC);
  }

  static SimpleSort sortBy(String propertyName, SimpleSort.Direction direction) {
    return SimpleSort.builder().propertyName(propertyName).direction(direction).build();
  }

  static <T> NativeSortWrapper<T> sortByNative(T nativeSort) {
    return NativeSortWrapper.<T>builder()
        .nativeSort(nativeSort)
        .build();
  }

  static SimpleSelect selectBy(String... propertyNames) {
    return SimpleSelect.of(propertyNames);
  }

  @SafeVarargs
  static <T> NativeSelectWrapper<T> selectByNative(T... nativeSelections) {
    return NativeSelectWrapper.<T>builder()
        .nativeSelections(Arrays.asList(nativeSelections))
        .build();
  }

  static Query wrapConditionInQuery(Condition condition) {
    return Querity.query()
        .filter(condition)
        .build();
  }

  /**
   * Create a reference to a field for use in field-to-field comparisons.
   *
   * @param fieldName the name of the field to reference
   * @return a FieldReference for use as a value in SimpleCondition
   */
  static FieldReference field(String fieldName) {
    return FieldReference.of(fieldName);
  }

  /**
   * Filter by comparing one field against another field.
   *
   * @param propertyName the property name on the left side
   * @param operator the comparison operator
   * @param fieldReference reference to the field on the right side
   * @return a SimpleCondition comparing two fields
   */
  static SimpleCondition filterByField(String propertyName, Operator operator, FieldReference fieldReference) {
    return SimpleCondition.builder()
        .propertyName(propertyName).operator(operator).value(fieldReference).build();
  }
}
