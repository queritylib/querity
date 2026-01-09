package io.github.queritylib.querity.parser;

import io.github.queritylib.querity.api.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class QueryVisitorTests {

  @Nested
  class FunctionParsingTests {

    @Test
    void givenAbsFunction_whenParse_thenReturnFunctionCall() {
      QueryDefinition query = QuerityParser.parseQuery("ABS(value) = 5");
      assertThat(query.getFilter()).isInstanceOf(SimpleCondition.class);
      SimpleCondition condition = (SimpleCondition) query.getFilter();
      assertThat(condition.getLeftExpression()).isInstanceOf(FunctionCall.class);
      FunctionCall fc = (FunctionCall) condition.getLeftExpression();
      assertThat(fc.getFunction()).isEqualTo(Function.ABS);
    }

    @Test
    void givenSqrtFunction_whenParse_thenReturnFunctionCall() {
      QueryDefinition query = QuerityParser.parseQuery("SQRT(value) >= 2");
      SimpleCondition condition = (SimpleCondition) query.getFilter();
      FunctionCall fc = (FunctionCall) condition.getLeftExpression();
      assertThat(fc.getFunction()).isEqualTo(Function.SQRT);
    }

    @Test
    void givenModFunction_whenParse_thenReturnFunctionCall() {
      QueryDefinition query = QuerityParser.parseQuery("MOD(value, 10) = 0");
      SimpleCondition condition = (SimpleCondition) query.getFilter();
      FunctionCall fc = (FunctionCall) condition.getLeftExpression();
      assertThat(fc.getFunction()).isEqualTo(Function.MOD);
      assertThat(fc.getArguments()).hasSize(2);
    }

    @Test
    void givenConcatFunction_whenParse_thenReturnFunctionCall() {
      QueryDefinition query = QuerityParser.parseQuery("CONCAT(firstName, lastName) STARTS WITH \"Jo\"");
      SimpleCondition condition = (SimpleCondition) query.getFilter();
      FunctionCall fc = (FunctionCall) condition.getLeftExpression();
      assertThat(fc.getFunction()).isEqualTo(Function.CONCAT);
    }

    @Test
    void givenSubstringFunction_whenParse_thenReturnFunctionCall() {
      QueryDefinition query = QuerityParser.parseQuery("SUBSTRING(name, 1, 3) = \"abc\"");
      SimpleCondition condition = (SimpleCondition) query.getFilter();
      FunctionCall fc = (FunctionCall) condition.getLeftExpression();
      assertThat(fc.getFunction()).isEqualTo(Function.SUBSTRING);
    }

    @Test
    void givenTrimFunction_whenParse_thenReturnFunctionCall() {
      QueryDefinition query = QuerityParser.parseQuery("TRIM(name) = \"test\"");
      SimpleCondition condition = (SimpleCondition) query.getFilter();
      FunctionCall fc = (FunctionCall) condition.getLeftExpression();
      assertThat(fc.getFunction()).isEqualTo(Function.TRIM);
    }

    @Test
    void givenLtrimFunction_whenParse_thenReturnFunctionCall() {
      QueryDefinition query = QuerityParser.parseQuery("LTRIM(name) = \"test\"");
      SimpleCondition condition = (SimpleCondition) query.getFilter();
      FunctionCall fc = (FunctionCall) condition.getLeftExpression();
      assertThat(fc.getFunction()).isEqualTo(Function.LTRIM);
    }

    @Test
    void givenRtrimFunction_whenParse_thenReturnFunctionCall() {
      QueryDefinition query = QuerityParser.parseQuery("RTRIM(name) = \"test\"");
      SimpleCondition condition = (SimpleCondition) query.getFilter();
      FunctionCall fc = (FunctionCall) condition.getLeftExpression();
      assertThat(fc.getFunction()).isEqualTo(Function.RTRIM);
    }

    @Test
    void givenLowerFunction_whenParse_thenReturnFunctionCall() {
      QueryDefinition query = QuerityParser.parseQuery("LOWER(name) = \"test\"");
      SimpleCondition condition = (SimpleCondition) query.getFilter();
      FunctionCall fc = (FunctionCall) condition.getLeftExpression();
      assertThat(fc.getFunction()).isEqualTo(Function.LOWER);
    }

    @Test
    void givenUpperFunction_whenParse_thenReturnFunctionCall() {
      QueryDefinition query = QuerityParser.parseQuery("UPPER(name) = \"TEST\"");
      SimpleCondition condition = (SimpleCondition) query.getFilter();
      FunctionCall fc = (FunctionCall) condition.getLeftExpression();
      assertThat(fc.getFunction()).isEqualTo(Function.UPPER);
    }

    @Test
    void givenLengthFunction_whenParse_thenReturnFunctionCall() {
      QueryDefinition query = QuerityParser.parseQuery("LENGTH(name) > 5");
      SimpleCondition condition = (SimpleCondition) query.getFilter();
      FunctionCall fc = (FunctionCall) condition.getLeftExpression();
      assertThat(fc.getFunction()).isEqualTo(Function.LENGTH);
    }

    @Test
    void givenLocateFunction_whenParse_thenReturnFunctionCall() {
      QueryDefinition query = QuerityParser.parseQuery("LOCATE(\"@\", email) > 0");
      SimpleCondition condition = (SimpleCondition) query.getFilter();
      FunctionCall fc = (FunctionCall) condition.getLeftExpression();
      assertThat(fc.getFunction()).isEqualTo(Function.LOCATE);
    }

    @Test
    void givenCoalesceFunction_whenParse_thenReturnFunctionCall() {
      QueryDefinition query = QuerityParser.parseQuery("COALESCE(nickname, firstName) = \"John\"");
      SimpleCondition condition = (SimpleCondition) query.getFilter();
      FunctionCall fc = (FunctionCall) condition.getLeftExpression();
      assertThat(fc.getFunction()).isEqualTo(Function.COALESCE);
    }

    @Test
    void givenNullifFunction_whenParse_thenReturnFunctionCall() {
      QueryDefinition query = QuerityParser.parseQuery("NULLIF(status, \"INACTIVE\") IS NOT NULL");
      SimpleCondition condition = (SimpleCondition) query.getFilter();
      FunctionCall fc = (FunctionCall) condition.getLeftExpression();
      assertThat(fc.getFunction()).isEqualTo(Function.NULLIF);
    }

    @Test
    void givenCountFunction_whenParse_thenReturnFunctionCall() {
      QueryDefinition query = QuerityParser.parseQuery("COUNT(id) > 0");
      SimpleCondition condition = (SimpleCondition) query.getFilter();
      FunctionCall fc = (FunctionCall) condition.getLeftExpression();
      assertThat(fc.getFunction()).isEqualTo(Function.COUNT);
    }

    @Test
    void givenSumFunction_whenParse_thenReturnFunctionCall() {
      QueryDefinition query = QuerityParser.parseQuery("SUM(amount) >= 100");
      SimpleCondition condition = (SimpleCondition) query.getFilter();
      FunctionCall fc = (FunctionCall) condition.getLeftExpression();
      assertThat(fc.getFunction()).isEqualTo(Function.SUM);
    }

    @Test
    void givenAvgFunction_whenParse_thenReturnFunctionCall() {
      QueryDefinition query = QuerityParser.parseQuery("AVG(price) < 50.5");
      SimpleCondition condition = (SimpleCondition) query.getFilter();
      FunctionCall fc = (FunctionCall) condition.getLeftExpression();
      assertThat(fc.getFunction()).isEqualTo(Function.AVG);
    }

    @Test
    void givenMinFunction_whenParse_thenReturnFunctionCall() {
      QueryDefinition query = QuerityParser.parseQuery("MIN(value) = 1");
      SimpleCondition condition = (SimpleCondition) query.getFilter();
      FunctionCall fc = (FunctionCall) condition.getLeftExpression();
      assertThat(fc.getFunction()).isEqualTo(Function.MIN);
    }

    @Test
    void givenMaxFunction_whenParse_thenReturnFunctionCall() {
      QueryDefinition query = QuerityParser.parseQuery("MAX(value) = 100");
      SimpleCondition condition = (SimpleCondition) query.getFilter();
      FunctionCall fc = (FunctionCall) condition.getLeftExpression();
      assertThat(fc.getFunction()).isEqualTo(Function.MAX);
    }
  }

  @Nested
  class NullaryFunctionTests {

    @Test
    void givenCurrentDateFunctionInSelect_whenParse_thenReturnFunctionCall() {
      QueryDefinition query = QuerityParser.parseQuery("SELECT CURRENT_DATE");
      assertThat(query).isInstanceOf(AdvancedQuery.class);
      AdvancedQuery advancedQuery = (AdvancedQuery) query;
      SimpleSelect select = (SimpleSelect) advancedQuery.getSelect();
      assertThat(select.getExpressions()).hasSize(1);
      FunctionCall fc = (FunctionCall) select.getExpressions().get(0);
      assertThat(fc.getFunction()).isEqualTo(Function.CURRENT_DATE);
    }

    @Test
    void givenCurrentTimeFunctionInSelect_whenParse_thenReturnFunctionCall() {
      QueryDefinition query = QuerityParser.parseQuery("SELECT CURRENT_TIME");
      assertThat(query).isInstanceOf(AdvancedQuery.class);
      AdvancedQuery advancedQuery = (AdvancedQuery) query;
      SimpleSelect select = (SimpleSelect) advancedQuery.getSelect();
      FunctionCall fc = (FunctionCall) select.getExpressions().get(0);
      assertThat(fc.getFunction()).isEqualTo(Function.CURRENT_TIME);
    }

    @Test
    void givenCurrentTimestampFunctionInSelect_whenParse_thenReturnFunctionCall() {
      QueryDefinition query = QuerityParser.parseQuery("SELECT CURRENT_TIMESTAMP");
      assertThat(query).isInstanceOf(AdvancedQuery.class);
      AdvancedQuery advancedQuery = (AdvancedQuery) query;
      SimpleSelect select = (SimpleSelect) advancedQuery.getSelect();
      FunctionCall fc = (FunctionCall) select.getExpressions().get(0);
      assertThat(fc.getFunction()).isEqualTo(Function.CURRENT_TIMESTAMP);
    }
  }

  @Nested
  class SelectWithFunctionTests {

    @Test
    void givenSelectWithFunction_whenParse_thenReturnSimpleSelectWithExpressions() {
      QueryDefinition query = QuerityParser.parseQuery("SELECT UPPER(name)");
      assertThat(query).isInstanceOf(AdvancedQuery.class);
      AdvancedQuery advancedQuery = (AdvancedQuery) query;
      assertThat(advancedQuery.getSelect()).isInstanceOf(SimpleSelect.class);
      SimpleSelect select = (SimpleSelect) advancedQuery.getSelect();
      assertThat(select.getExpressions()).hasSize(1);
      assertThat(select.getExpressions().get(0)).isInstanceOf(FunctionCall.class);
    }

    @Test
    void givenSelectWithMixedExpressions_whenParse_thenReturnSimpleSelectWithExpressions() {
      QueryDefinition query = QuerityParser.parseQuery("SELECT id, UPPER(name), email");
      assertThat(query).isInstanceOf(AdvancedQuery.class);
      AdvancedQuery advancedQuery = (AdvancedQuery) query;
      SimpleSelect select = (SimpleSelect) advancedQuery.getSelect();
      assertThat(select.getExpressions()).hasSize(3);
    }

    @Test
    void givenSelectWithFunctionAlias_whenParse_thenReturnFunctionWithAlias() {
      QueryDefinition query = QuerityParser.parseQuery("SELECT UPPER(name) AS upperName");
      assertThat(query).isInstanceOf(AdvancedQuery.class);
      AdvancedQuery advancedQuery = (AdvancedQuery) query;
      SimpleSelect select = (SimpleSelect) advancedQuery.getSelect();
      FunctionCall fc = (FunctionCall) select.getExpressions().get(0);
      assertThat(fc.getAlias()).isEqualTo("upperName");
    }

    @Test
    void givenSelectWithPropertyAlias_whenParse_thenReturnPropertyReferenceWithAlias() {
      QueryDefinition query = QuerityParser.parseQuery("SELECT name AS displayName");
      assertThat(query).isInstanceOf(AdvancedQuery.class);
      AdvancedQuery advancedQuery = (AdvancedQuery) query;
      SimpleSelect select = (SimpleSelect) advancedQuery.getSelect();
      assertThat(select.hasExpressions()).isTrue();
      PropertyReference pr = (PropertyReference) select.getExpressions().get(0);
      assertThat(pr.getPropertyName()).isEqualTo("name");
      assertThat(pr.getAlias()).isEqualTo("displayName");
    }
  }

  @Nested
  class SortWithFunctionTests {

    @Test
    void givenSortByFunction_whenParse_thenReturnSimpleSortWithExpression() {
      QueryDefinition query = QuerityParser.parseQuery("SORT BY UPPER(name) ASC");
      assertThat(query.getSort()).hasSize(1);
      SimpleSort sort = (SimpleSort) query.getSort().get(0);
      assertThat(sort.getExpression()).isInstanceOf(FunctionCall.class);
    }

    @Test
    void givenSortByFunctionDesc_whenParse_thenReturnSimpleSortWithDescDirection() {
      QueryDefinition query = QuerityParser.parseQuery("SORT BY LENGTH(name) DESC");
      SimpleSort sort = (SimpleSort) query.getSort().get(0);
      assertThat(sort.getDirection()).isEqualTo(SimpleSort.Direction.DESC);
    }
  }

  @Nested
  class ValueParsingTests {

    @Test
    void givenIntValue_whenParse_thenReturnInteger() {
      QueryDefinition query = QuerityParser.parseQuery("value = 42");
      SimpleCondition condition = (SimpleCondition) query.getFilter();
      assertThat(condition.getValue()).isEqualTo(42);
    }

    @Test
    void givenDecimalValue_whenParse_thenReturnBigDecimal() {
      QueryDefinition query = QuerityParser.parseQuery("price = 19.99");
      SimpleCondition condition = (SimpleCondition) query.getFilter();
      assertThat(condition.getValue()).isEqualTo(new BigDecimal("19.99"));
    }

    @Test
    void givenBooleanTrueValue_whenParse_thenReturnBoolean() {
      QueryDefinition query = QuerityParser.parseQuery("active = true");
      SimpleCondition condition = (SimpleCondition) query.getFilter();
      assertThat(condition.getValue()).isEqualTo(true);
    }

    @Test
    void givenBooleanFalseValue_whenParse_thenReturnBoolean() {
      QueryDefinition query = QuerityParser.parseQuery("active = false");
      SimpleCondition condition = (SimpleCondition) query.getFilter();
      assertThat(condition.getValue()).isEqualTo(false);
    }

    @Test
    void givenStringValue_whenParse_thenReturnString() {
      QueryDefinition query = QuerityParser.parseQuery("name = \"John\"");
      SimpleCondition condition = (SimpleCondition) query.getFilter();
      assertThat(condition.getValue()).isEqualTo("John");
    }

    @Test
    void givenArrayValue_whenParse_thenReturnArray() {
      QueryDefinition query = QuerityParser.parseQuery("status IN (\"active\", \"pending\")");
      SimpleCondition condition = (SimpleCondition) query.getFilter();
      assertThat(condition.getValue()).isInstanceOf(Object[].class);
      Object[] values = (Object[]) condition.getValue();
      assertThat(values).containsExactly("active", "pending");
    }
  }

  @Nested
  class OperatorParsingTests {

    @Test
    void givenEqualsOperator_whenParse_thenReturnEqualsOperator() {
      QueryDefinition query = QuerityParser.parseQuery("name = \"test\"");
      SimpleCondition condition = (SimpleCondition) query.getFilter();
      assertThat(condition.getOperator()).isEqualTo(Operator.EQUALS);
    }

    @Test
    void givenNotEqualsOperator_whenParse_thenReturnNotEqualsOperator() {
      QueryDefinition query = QuerityParser.parseQuery("name != \"test\"");
      SimpleCondition condition = (SimpleCondition) query.getFilter();
      assertThat(condition.getOperator()).isEqualTo(Operator.NOT_EQUALS);
    }

    @Test
    void givenGreaterThanOperator_whenParse_thenReturnGreaterThanOperator() {
      QueryDefinition query = QuerityParser.parseQuery("value > 5");
      SimpleCondition condition = (SimpleCondition) query.getFilter();
      assertThat(condition.getOperator()).isEqualTo(Operator.GREATER_THAN);
    }

    @Test
    void givenLessThanOperator_whenParse_thenReturnLessThanOperator() {
      QueryDefinition query = QuerityParser.parseQuery("value < 5");
      SimpleCondition condition = (SimpleCondition) query.getFilter();
      assertThat(condition.getOperator()).isEqualTo(Operator.LESSER_THAN);
    }

    @Test
    void givenGreaterThanEqualsOperator_whenParse_thenReturnGreaterThanEqualsOperator() {
      QueryDefinition query = QuerityParser.parseQuery("value >= 5");
      SimpleCondition condition = (SimpleCondition) query.getFilter();
      assertThat(condition.getOperator()).isEqualTo(Operator.GREATER_THAN_EQUALS);
    }

    @Test
    void givenLessThanEqualsOperator_whenParse_thenReturnLessThanEqualsOperator() {
      QueryDefinition query = QuerityParser.parseQuery("value <= 5");
      SimpleCondition condition = (SimpleCondition) query.getFilter();
      assertThat(condition.getOperator()).isEqualTo(Operator.LESSER_THAN_EQUALS);
    }

    @Test
    void givenStartsWithOperator_whenParse_thenReturnStartsWithOperator() {
      QueryDefinition query = QuerityParser.parseQuery("name STARTS WITH \"test\"");
      SimpleCondition condition = (SimpleCondition) query.getFilter();
      assertThat(condition.getOperator()).isEqualTo(Operator.STARTS_WITH);
    }

    @Test
    void givenEndsWithOperator_whenParse_thenReturnEndsWithOperator() {
      QueryDefinition query = QuerityParser.parseQuery("name ENDS WITH \"test\"");
      SimpleCondition condition = (SimpleCondition) query.getFilter();
      assertThat(condition.getOperator()).isEqualTo(Operator.ENDS_WITH);
    }

    @Test
    void givenContainsOperator_whenParse_thenReturnContainsOperator() {
      QueryDefinition query = QuerityParser.parseQuery("name CONTAINS \"test\"");
      SimpleCondition condition = (SimpleCondition) query.getFilter();
      assertThat(condition.getOperator()).isEqualTo(Operator.CONTAINS);
    }

    @Test
    void givenIsNullOperator_whenParse_thenReturnIsNullOperator() {
      QueryDefinition query = QuerityParser.parseQuery("name IS NULL");
      SimpleCondition condition = (SimpleCondition) query.getFilter();
      assertThat(condition.getOperator()).isEqualTo(Operator.IS_NULL);
    }

    @Test
    void givenIsNotNullOperator_whenParse_thenReturnIsNotNullOperator() {
      QueryDefinition query = QuerityParser.parseQuery("name IS NOT NULL");
      SimpleCondition condition = (SimpleCondition) query.getFilter();
      assertThat(condition.getOperator()).isEqualTo(Operator.IS_NOT_NULL);
    }

    @Test
    void givenInOperator_whenParse_thenReturnInOperator() {
      QueryDefinition query = QuerityParser.parseQuery("status IN (\"A\", \"B\")");
      SimpleCondition condition = (SimpleCondition) query.getFilter();
      assertThat(condition.getOperator()).isEqualTo(Operator.IN);
    }

    @Test
    void givenNotInOperator_whenParse_thenReturnNotInOperator() {
      QueryDefinition query = QuerityParser.parseQuery("status NOT IN (\"A\", \"B\")");
      SimpleCondition condition = (SimpleCondition) query.getFilter();
      assertThat(condition.getOperator()).isEqualTo(Operator.NOT_IN);
    }
  }

  @Nested
  class ConditionWrapperTests {

    @Test
    void givenAndCondition_whenParse_thenReturnAndConditionsWrapper() {
      QueryDefinition query = QuerityParser.parseQuery("AND(name = \"test\", age > 18)");
      assertThat(query.getFilter()).isInstanceOf(AndConditionsWrapper.class);
    }

    @Test
    void givenOrCondition_whenParse_thenReturnOrConditionsWrapper() {
      QueryDefinition query = QuerityParser.parseQuery("OR(status = \"active\", status = \"pending\")");
      assertThat(query.getFilter()).isInstanceOf(OrConditionsWrapper.class);
    }

    @Test
    void givenNotCondition_whenParse_thenReturnNotCondition() {
      QueryDefinition query = QuerityParser.parseQuery("NOT(status = \"deleted\")");
      assertThat(query.getFilter()).isInstanceOf(NotCondition.class);
    }
  }

  @Nested
  class DistinctAndPaginationTests {

    @Test
    void givenDistinctQuery_whenParse_thenReturnDistinctTrue() {
      QueryDefinition query = QuerityParser.parseQuery("DISTINCT name = \"test\"");
      assertThat(query).isInstanceOf(Query.class);
      assertThat(((Query) query).isDistinct()).isTrue();
    }

    @Test
    void givenPaginationQuery_whenParse_thenReturnPagination() {
      QueryDefinition query = QuerityParser.parseQuery("name = \"test\" PAGE 1, 20");
      assertThat(query.getPagination()).isNotNull();
      assertThat(query.getPagination().getPage()).isEqualTo(1);
      assertThat(query.getPagination().getPageSize()).isEqualTo(20);
    }
  }

  @Nested
  class FieldToFieldComparisonTests {

    @Test
    void givenFieldToFieldComparison_whenParse_thenReturnFieldReference() {
      QueryDefinition query = QuerityParser.parseQuery("price = originalPrice");
      SimpleCondition condition = (SimpleCondition) query.getFilter();
      assertThat(condition.isFieldReference()).isTrue();
      FieldReference ref = condition.getFieldReference();
      assertThat(ref.getFieldName()).isEqualTo("originalPrice");
    }
  }

  @Nested
  class QuotedPropertyTests {

    @Test
    void givenQuotedPropertyInFilter_whenParse_thenReturnPropertyName() {
      QueryDefinition query = QuerityParser.parseQuery("`count` = 5");
      SimpleCondition condition = (SimpleCondition) query.getFilter();
      assertThat(condition.getPropertyName()).isEqualTo("count");
    }

    @Test
    void givenQuotedPropertyFieldReference_whenParse_thenReturnFieldReference() {
      QueryDefinition query = QuerityParser.parseQuery("price = `sum`");
      SimpleCondition condition = (SimpleCondition) query.getFilter();
      assertThat(condition.isFieldReference()).isTrue();
      assertThat(condition.getFieldReference().getFieldName()).isEqualTo("sum");
    }

    @Test
    void givenQuotedPropertyInSelect_whenParse_thenReturnPropertyNames() {
      QueryDefinition query = QuerityParser.parseQuery("SELECT `count`, name");
      assertThat(query).isInstanceOf(AdvancedQuery.class);
      AdvancedQuery advancedQuery = (AdvancedQuery) query;
      SimpleSelect select = (SimpleSelect) advancedQuery.getSelect();
      assertThat(select.getPropertyNames()).containsExactly("count", "name");
    }

    @Test
    void givenQuotedPropertyInSort_whenParse_thenReturnSortProperty() {
      QueryDefinition query = QuerityParser.parseQuery("SORT BY `count` DESC");
      SimpleSort sort = (SimpleSort) query.getSort().get(0);
      assertThat(sort.getPropertyName()).isEqualTo("count");
    }
  }

  @Nested
  class SortDirectionTests {

    @Test
    void givenSortAsc_whenParse_thenReturnAscDirection() {
      QueryDefinition query = QuerityParser.parseQuery("SORT BY name ASC");
      SimpleSort sort = (SimpleSort) query.getSort().get(0);
      assertThat(sort.getDirection()).isEqualTo(SimpleSort.Direction.ASC);
    }

    @Test
    void givenSortDesc_whenParse_thenReturnDescDirection() {
      QueryDefinition query = QuerityParser.parseQuery("SORT BY name DESC");
      SimpleSort sort = (SimpleSort) query.getSort().get(0);
      assertThat(sort.getDirection()).isEqualTo(SimpleSort.Direction.DESC);
    }

    @Test
    void givenSortWithoutDirection_whenParse_thenReturnAscDirectionByDefault() {
      QueryDefinition query = QuerityParser.parseQuery("SORT BY name");
      SimpleSort sort = (SimpleSort) query.getSort().get(0);
      assertThat(sort.getDirection()).isEqualTo(SimpleSort.Direction.ASC);
    }
  }

  @Nested
  class SelectOnlyPropertyReferencesTests {

    @Test
    void givenSelectWithOnlyPropertyReferences_whenParse_thenReturnPropertyNames() {
      QueryDefinition query = QuerityParser.parseQuery("SELECT id, name, email");
      assertThat(query).isInstanceOf(AdvancedQuery.class);
      AdvancedQuery advancedQuery = (AdvancedQuery) query;
      SimpleSelect select = (SimpleSelect) advancedQuery.getSelect();
      assertThat(select.getPropertyNames()).containsExactly("id", "name", "email");
    }
  }
}
