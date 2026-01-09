package io.github.queritylib.querity.api;

import org.junit.jupiter.api.Test;

import static io.github.queritylib.querity.api.Operator.EQUALS;
import static io.github.queritylib.querity.api.Operator.GREATER_THAN;
import static io.github.queritylib.querity.api.Querity.*;
import static io.github.queritylib.querity.api.SimpleSort.Direction.ASC;
import static io.github.queritylib.querity.api.SimpleSort.Direction.DESC;
import static org.assertj.core.api.Assertions.assertThat;

class QuerityFunctionFactoryTests {

  // Property factory tests

  @Test
  void givenPropertyName_whenProperty_thenReturnPropertyReference() {
    PropertyReference ref = property("lastName");

    assertThat(ref.getPropertyName()).isEqualTo("lastName");
  }  @Test
  void givenPropertyName_whenProp_thenReturnPropertyReference() {
    PropertyReference ref = prop("lastName");
    assertThat(ref.getPropertyName()).isEqualTo("lastName");
  }

  // Arithmetic function tests

  @Test
  void givenExpression_whenAbs_thenReturnFunctionCall() {
    FunctionCall fc = abs(prop("value"));

    assertThat(fc.getFunction()).isEqualTo(Function.ABS);
    assertThat(fc.getArguments()).hasSize(1);
  }

  @Test
  void givenExpression_whenSqrt_thenReturnFunctionCall() {
    FunctionCall fc = sqrt(prop("area"));

    assertThat(fc.getFunction()).isEqualTo(Function.SQRT);
  }

  @Test
  void givenTwoExpressions_whenMod_thenReturnFunctionCall() {
    FunctionCall fc = mod(prop("value"), lit(10));

    assertThat(fc.getFunction()).isEqualTo(Function.MOD);
    assertThat(fc.getArguments()).hasSize(2);
  }

  // String function tests

  @Test
  void givenExpressions_whenConcat_thenReturnFunctionCall() {
    FunctionCall fc = concat(prop("firstName"), lit(" "), prop("lastName"));

    assertThat(fc.getFunction()).isEqualTo(Function.CONCAT);
    assertThat(fc.getArguments()).hasSize(3);
  }

  @Test
  void givenExpression_whenSubstring_thenReturnFunctionCall() {
    FunctionCall fc = substring(prop("name"), 1, 5);

    assertThat(fc.getFunction()).isEqualTo(Function.SUBSTRING);
    assertThat(fc.getArguments()).hasSize(3);
  }

  @Test
  void givenExpression_whenTrim_thenReturnFunctionCall() {
    FunctionCall fc = trim(prop("name"));

    assertThat(fc.getFunction()).isEqualTo(Function.TRIM);
  }

  @Test
  void givenExpression_whenLtrim_thenReturnFunctionCall() {
    FunctionCall fc = ltrim(prop("name"));

    assertThat(fc.getFunction()).isEqualTo(Function.LTRIM);
  }

  @Test
  void givenExpression_whenRtrim_thenReturnFunctionCall() {
    FunctionCall fc = rtrim(prop("name"));

    assertThat(fc.getFunction()).isEqualTo(Function.RTRIM);
  }

  @Test
  void givenExpression_whenLower_thenReturnFunctionCall() {
    FunctionCall fc = lower(prop("name"));

    assertThat(fc.getFunction()).isEqualTo(Function.LOWER);
  }

  @Test
  void givenExpression_whenUpper_thenReturnFunctionCall() {
    FunctionCall fc = upper(prop("name"));

    assertThat(fc.getFunction()).isEqualTo(Function.UPPER);
  }

  @Test
  void givenExpression_whenLength_thenReturnFunctionCall() {
    FunctionCall fc = length(prop("description"));

    assertThat(fc.getFunction()).isEqualTo(Function.LENGTH);
  }

  @Test
  void givenExpressions_whenLocate_thenReturnFunctionCall() {
    FunctionCall fc = locate(lit("@"), prop("email"));

    assertThat(fc.getFunction()).isEqualTo(Function.LOCATE);
    assertThat(fc.getArguments()).hasSize(2);
  }

  // Date/Time function tests

  @Test
  void whenCurrentDate_thenReturnFunctionCall() {
    FunctionCall fc = currentDate();

    assertThat(fc.getFunction()).isEqualTo(Function.CURRENT_DATE);
    assertThat(fc.getArguments()).isEmpty();
  }

  @Test
  void whenCurrentTime_thenReturnFunctionCall() {
    FunctionCall fc = currentTime();

    assertThat(fc.getFunction()).isEqualTo(Function.CURRENT_TIME);
  }

  @Test
  void whenCurrentTimestamp_thenReturnFunctionCall() {
    FunctionCall fc = currentTimestamp();

    assertThat(fc.getFunction()).isEqualTo(Function.CURRENT_TIMESTAMP);
  }

  // Conditional function tests

  @Test
  void givenExpressions_whenCoalesce_thenReturnFunctionCall() {
    FunctionCall fc = coalesce(prop("nickname"), prop("firstName"), lit("Unknown"));

    assertThat(fc.getFunction()).isEqualTo(Function.COALESCE);
    assertThat(fc.getArguments()).hasSize(3);
  }

  @Test
  void givenTwoExpressions_whenNullif_thenReturnFunctionCall() {
    FunctionCall fc = nullif(prop("status"), lit("INACTIVE"));

    assertThat(fc.getFunction()).isEqualTo(Function.NULLIF);
    assertThat(fc.getArguments()).hasSize(2);
  }

  // Aggregate function tests

  @Test
  void givenExpression_whenCount_thenReturnFunctionCall() {
    FunctionCall fc = count(prop("id"));

    assertThat(fc.getFunction()).isEqualTo(Function.COUNT);
  }

  @Test
  void givenExpression_whenSum_thenReturnFunctionCall() {
    FunctionCall fc = sum(prop("amount"));

    assertThat(fc.getFunction()).isEqualTo(Function.SUM);
  }

  @Test
  void givenExpression_whenAvg_thenReturnFunctionCall() {
    FunctionCall fc = avg(prop("price"));

    assertThat(fc.getFunction()).isEqualTo(Function.AVG);
  }

  @Test
  void givenExpression_whenMin_thenReturnFunctionCall() {
    FunctionCall fc = min(prop("date"));

    assertThat(fc.getFunction()).isEqualTo(Function.MIN);
  }

  @Test
  void givenExpression_whenMax_thenReturnFunctionCall() {
    FunctionCall fc = max(prop("date"));

    assertThat(fc.getFunction()).isEqualTo(Function.MAX);
  }

  // Generic function factory test

  @Test
  void givenFunctionAndArgs_whenFunction_thenReturnFunctionCall() {
    FunctionCall fc = function(Function.UPPER, prop("name"));

    assertThat(fc.getFunction()).isEqualTo(Function.UPPER);
    assertThat(fc.getArguments()).hasSize(1);
  }

  // filterBy with expression tests

  @Test
  void givenExpressionAndValue_whenFilterBy_thenReturnSimpleCondition() {
    SimpleCondition condition = filterBy(upper(prop("name")), EQUALS, "JOHN");

    assertThat(condition.hasLeftExpression()).isTrue();
    assertThat(condition.getLeftExpression()).isInstanceOf(FunctionCall.class);
    assertThat(condition.getOperator()).isEqualTo(EQUALS);
    assertThat(condition.getValue()).isEqualTo("JOHN");
  }

  @Test
  void givenExpressionAndOperatorAndValue_whenFilterBy_thenReturnSimpleCondition() {
    SimpleCondition condition = filterBy(length(prop("name")), GREATER_THAN, 5);

    assertThat(condition.hasLeftExpression()).isTrue();
    assertThat(condition.getOperator()).isEqualTo(GREATER_THAN);
    assertThat(condition.getValue()).isEqualTo(5);
  }

  // sortBy with expression tests

  @Test
  void givenExpression_whenSortBy_thenReturnSimpleSortAscending() {
    SimpleSort sort = sortBy(upper(prop("name")));

    assertThat(sort.hasExpression()).isTrue();
    assertThat(sort.getDirection()).isEqualTo(ASC);
  }

  @Test
  void givenExpressionAndDirection_whenSortBy_thenReturnSimpleSortWithDirection() {
    SimpleSort sort = sortBy(length(prop("name")), DESC);

    assertThat(sort.hasExpression()).isTrue();
    assertThat(sort.getDirection()).isEqualTo(DESC);
  }

  // selectBy with expressions tests

  @Test
  void givenExpressions_whenSelectBy_thenReturnSimpleSelect() {
    SimpleSelect select = selectBy(
        prop("id"),
        upper(prop("name")).as("upperName"),
        length(prop("description")).as("descLen")
    );

    assertThat(select.hasExpressions()).isTrue();
    assertThat(select.getExpressions()).hasSize(3);
  }

  // Integration test: building a complete query with functions

  @Test
  void givenAdvancedQueryWithFunctions_whenBuild_thenReturnCorrectQuery() {
    AdvancedQuery query = advancedQuery()
        .filter(filterBy(upper(prop("lastName")), EQUALS, "SKYWALKER"))
        .sort(sortBy(length(prop("firstName")), ASC))
        .select(selectBy(
            prop("id"),
            upper(prop("firstName")).as("upperFirst"),
            length(prop("lastName")).as("lastNameLen")
        ))
        .pagination(0, 10)
        .build();

    assertThat(query.getFilter()).isNotNull();
    assertThat(query.getSort()).hasSize(1);
    assertThat(query.getSelect()).isNotNull();
    assertThat(query.getPagination()).isNotNull();
  }
}
