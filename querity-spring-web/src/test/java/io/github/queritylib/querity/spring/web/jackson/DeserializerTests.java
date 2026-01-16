package io.github.queritylib.querity.spring.web.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.queritylib.querity.api.*;
import io.github.queritylib.querity.api.Select;
import io.github.queritylib.querity.api.SimpleSelect;
import io.github.queritylib.querity.api.SimpleSort;
import io.github.queritylib.querity.api.Sort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DeserializerTests {

  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new QuerityModule());
  }

  @Nested
  class QueryDeserializerTests {
    @Test
    void givenEmptyQueryJson_whenDeserialize_thenReturnEmptyQuery() throws Exception {
      String json = "{}";

      Query query = objectMapper.readValue(json, Query.class);

      assertThat(query).isNotNull();
      assertThat(query.hasFilter()).isFalse();
    }

    @Test
    void givenQueryWithFilterJson_whenDeserialize_thenReturnQuery() throws Exception {
      String json = "{\"filter\":{\"propertyName\":\"lastName\",\"operator\":\"EQUALS\",\"value\":\"Skywalker\"}}";

      Query query = objectMapper.readValue(json, Query.class);

      assertThat(query).isNotNull();
      assertThat(query.hasFilter()).isTrue();
    }

    @Test
    void givenAdvancedQueryWithSelectJson_whenDeserialize_thenReturnAdvancedQuery() throws Exception {
      String json = "{\"select\":{\"propertyNames\":[\"id\",\"name\"]}}";

      AdvancedQuery query = objectMapper.readValue(json, AdvancedQuery.class);

      assertThat(query).isNotNull();
      assertThat(query.hasSelect()).isTrue();
    }

    @Test
    void givenAdvancedQueryWithGroupByPropertyNames_whenDeserialize_thenReturnAdvancedQueryWithGroupBy() throws Exception {
      String json = "{\"groupBy\":{\"propertyNames\":[\"category\",\"region\"]}}";

      AdvancedQuery query = objectMapper.readValue(json, AdvancedQuery.class);

      assertThat(query).isNotNull();
      assertThat(query.hasGroupBy()).isTrue();
      assertThat(query.getGroupBy()).isInstanceOf(SimpleGroupBy.class);
      SimpleGroupBy groupBy = (SimpleGroupBy) query.getGroupBy();
      assertThat(groupBy.getPropertyNames()).containsExactly("category", "region");
    }

    @Test
    void givenAdvancedQueryWithGroupByExpressions_whenDeserialize_thenReturnAdvancedQueryWithGroupBy() throws Exception {
      String json = "{\"groupBy\":{\"expressions\":[{\"function\":\"UPPER\",\"arguments\":[{\"propertyName\":\"category\"}]}]}}";

      AdvancedQuery query = objectMapper.readValue(json, AdvancedQuery.class);

      assertThat(query).isNotNull();
      assertThat(query.hasGroupBy()).isTrue();
      assertThat(query.getGroupBy()).isInstanceOf(SimpleGroupBy.class);
      SimpleGroupBy groupBy = (SimpleGroupBy) query.getGroupBy();
      assertThat(groupBy.getExpressions()).hasSize(1);
    }

    @Test
    void givenAdvancedQueryWithHaving_whenDeserialize_thenReturnAdvancedQueryWithHaving() throws Exception {
      String json = "{\"groupBy\":{\"propertyNames\":[\"category\"]},\"having\":{\"propertyName\":\"count\",\"operator\":\"GREATER_THAN\",\"value\":5}}";

      AdvancedQuery query = objectMapper.readValue(json, AdvancedQuery.class);

      assertThat(query).isNotNull();
      assertThat(query.hasGroupBy()).isTrue();
      assertThat(query.hasHaving()).isTrue();
      assertThat(query.getHaving()).isInstanceOf(SimpleCondition.class);
    }

    @Test
    void givenAdvancedQueryWithGroupByAndSelectAndFilter_whenDeserialize_thenReturnCompleteAdvancedQuery() throws Exception {
      String json = "{\"filter\":{\"propertyName\":\"status\",\"operator\":\"EQUALS\",\"value\":\"ACTIVE\"}," +
          "\"select\":{\"propertyNames\":[\"category\",\"total\"]}," +
          "\"groupBy\":{\"propertyNames\":[\"category\"]}," +
          "\"having\":{\"propertyName\":\"total\",\"operator\":\"GREATER_THAN\",\"value\":100}}";

      AdvancedQuery query = objectMapper.readValue(json, AdvancedQuery.class);

      assertThat(query).isNotNull();
      assertThat(query.hasFilter()).isTrue();
      assertThat(query.hasSelect()).isTrue();
      assertThat(query.hasGroupBy()).isTrue();
      assertThat(query.hasHaving()).isTrue();
    }
  }

  @Nested
  class SelectDeserializerTests {
    @Test
    void givenSimpleSelectJson_whenDeserialize_thenReturnSimpleSelect() throws Exception {
      String json = "{\"propertyNames\":[\"id\",\"name\",\"email\"]}";

      Select select = objectMapper.readValue(json, Select.class);

      assertThat(select).isInstanceOf(SimpleSelect.class);
      assertThat(select.getPropertyNames()).containsExactly("id", "name", "email");
    }

    @Test
    void givenSinglePropertySelectJson_whenDeserialize_thenReturnSimpleSelect() throws Exception {
      String json = "{\"propertyNames\":[\"id\"]}";

      Select select = objectMapper.readValue(json, Select.class);

      assertThat(select).isInstanceOf(SimpleSelect.class);
      assertThat(select.getPropertyNames()).containsExactly("id");
    }

    @Test
    void givenSelectWithExpressionsJson_whenDeserialize_thenReturnSimpleSelectWithExpressions() throws Exception {
      String json = "{\"expressions\":[{\"propertyName\":\"id\"},{\"function\":\"UPPER\",\"arguments\":[{\"propertyName\":\"name\"}]}]}";

      Select select = objectMapper.readValue(json, Select.class);

      assertThat(select).isInstanceOf(SimpleSelect.class);
      SimpleSelect simpleSelect = (SimpleSelect) select;
      assertThat(simpleSelect.hasExpressions()).isTrue();
      assertThat(simpleSelect.getExpressions()).hasSize(2);
      assertThat(simpleSelect.getExpressions().get(0)).isInstanceOf(PropertyReference.class);
      assertThat(simpleSelect.getExpressions().get(1)).isInstanceOf(FunctionCall.class);
    }

    @Test
    void givenSelectWithAggregationExpressionsJson_whenDeserialize_thenReturnSimpleSelect() throws Exception {
      String json = "{\"expressions\":[{\"function\":\"COUNT\",\"arguments\":[{\"propertyName\":\"id\"}],\"alias\":\"total\"}]}";

      Select select = objectMapper.readValue(json, Select.class);

      assertThat(select).isInstanceOf(SimpleSelect.class);
      SimpleSelect simpleSelect = (SimpleSelect) select;
      assertThat(simpleSelect.hasExpressions()).isTrue();
      assertThat(simpleSelect.getExpressions()).hasSize(1);
      FunctionCall func = (FunctionCall) simpleSelect.getExpressions().get(0);
      assertThat(func.getFunction()).isEqualTo(Function.COUNT);
      assertThat(func.getAlias()).isEqualTo("total");
    }

    @Test
    void givenInvalidSelectJson_whenDeserialize_thenThrowException() {
      String json = "{\"unknownField\":\"value\"}";

      assertThatThrownBy(() -> objectMapper.readValue(json, Select.class))
          .isInstanceOf(IllegalArgumentException.class);
    }
  }

  @Nested
  class SortDeserializerTests {
    @Test
    void givenSimpleSortJsonWithDirection_whenDeserialize_thenReturnSimpleSort() throws Exception {
      String json = "{\"propertyName\":\"lastName\",\"direction\":\"DESC\"}";

      Sort sort = objectMapper.readValue(json, Sort.class);

      assertThat(sort).isInstanceOf(SimpleSort.class);
      SimpleSort simpleSort = (SimpleSort) sort;
      assertThat(simpleSort.getPropertyName()).isEqualTo("lastName");
      assertThat(simpleSort.getDirection()).isEqualTo(SimpleSort.Direction.DESC);
    }

    @Test
    void givenSimpleSortJsonWithoutDirection_whenDeserialize_thenReturnSimpleSortWithDefaultAsc() throws Exception {
      String json = "{\"propertyName\":\"lastName\"}";

      Sort sort = objectMapper.readValue(json, Sort.class);

      assertThat(sort).isInstanceOf(SimpleSort.class);
      SimpleSort simpleSort = (SimpleSort) sort;
      assertThat(simpleSort.getPropertyName()).isEqualTo("lastName");
      assertThat(simpleSort.getDirection()).isEqualTo(SimpleSort.Direction.ASC);
    }

    @Test
    void givenInvalidSortJson_whenDeserialize_thenThrowException() {
      String json = "{\"unknownField\":\"value\"}";

      assertThatThrownBy(() -> objectMapper.readValue(json, Sort.class))
          .isInstanceOf(IllegalArgumentException.class);
    }
  }

  @Nested
  class QuerityModuleTests {
    @Test
    void givenQuerityModule_whenGetModuleName_thenReturnClassName() {
      QuerityModule module = new QuerityModule();

      assertThat(module.getModuleName()).isEqualTo("QuerityModule");
    }

    @Test
    void givenQuerityModule_whenGetVersion_thenReturnUnknownVersion() {
      QuerityModule module = new QuerityModule();

      assertThat(module.version()).isNotNull();
    }
  }

  @Nested
  class PropertyExpressionDeserializerTests {
    @Test
    void givenPropertyReferenceJson_whenDeserialize_thenReturnPropertyReference() throws Exception {
      String json = "{\"propertyName\":\"lastName\"}";

      PropertyExpression expr = objectMapper.readValue(json, PropertyExpression.class);

      assertThat(expr).isInstanceOf(PropertyReference.class);
      PropertyReference ref = (PropertyReference) expr;
      assertThat(ref.getPropertyName()).isEqualTo("lastName");
    }

    @Test
    void givenPropertyReferenceWithAliasJson_whenDeserialize_thenReturnPropertyReferenceWithAlias() throws Exception {
      String json = "{\"propertyName\":\"firstName\",\"alias\":\"name\"}";

      PropertyExpression expr = objectMapper.readValue(json, PropertyExpression.class);

      assertThat(expr).isInstanceOf(PropertyReference.class);
      PropertyReference ref = (PropertyReference) expr;
      assertThat(ref.getPropertyName()).isEqualTo("firstName");
      assertThat(ref.getAlias()).isEqualTo("name");
    }

    @Test
    void givenFunctionCallJson_whenDeserialize_thenReturnFunctionCall() throws Exception {
      String json = "{\"function\":\"UPPER\",\"arguments\":[{\"propertyName\":\"lastName\"}]}";

      PropertyExpression expr = objectMapper.readValue(json, PropertyExpression.class);

      assertThat(expr).isInstanceOf(FunctionCall.class);
      FunctionCall func = (FunctionCall) expr;
      assertThat(func.getFunction()).isEqualTo(Function.UPPER);
    }

    @Test
    void givenInvalidPropertyExpressionJson_whenDeserialize_thenThrowException() {
      String json = "{\"unknownField\":\"value\"}";

      assertThatThrownBy(() -> objectMapper.readValue(json, PropertyExpression.class))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Unknown PropertyExpression type");
    }
  }

  @Nested
  class FunctionArgumentDeserializerTests {
    @Test
    void givenPropertyReferenceJson_whenDeserialize_thenReturnPropertyReference() throws Exception {
      String json = "{\"propertyName\":\"lastName\"}";

      FunctionArgument arg = objectMapper.readValue(json, FunctionArgument.class);

      assertThat(arg).isInstanceOf(PropertyReference.class);
      PropertyReference ref = (PropertyReference) arg;
      assertThat(ref.getPropertyName()).isEqualTo("lastName");
    }

    @Test
    void givenFunctionCallJson_whenDeserialize_thenReturnFunctionCall() throws Exception {
      String json = "{\"function\":\"LOWER\",\"arguments\":[{\"propertyName\":\"name\"}]}";

      FunctionArgument arg = objectMapper.readValue(json, FunctionArgument.class);

      assertThat(arg).isInstanceOf(FunctionCall.class);
      FunctionCall func = (FunctionCall) arg;
      assertThat(func.getFunction()).isEqualTo(Function.LOWER);
    }

    @Test
    void givenLiteralStringJson_whenDeserialize_thenReturnLiteral() throws Exception {
      String json = "{\"value\":\"hello\"}";

      FunctionArgument arg = objectMapper.readValue(json, FunctionArgument.class);

      assertThat(arg).isInstanceOf(Literal.class);
      Literal lit = (Literal) arg;
      assertThat(lit.getValue()).isEqualTo("hello");
    }

    @Test
    void givenLiteralNumberJson_whenDeserialize_thenReturnLiteral() throws Exception {
      String json = "{\"value\":42}";

      FunctionArgument arg = objectMapper.readValue(json, FunctionArgument.class);

      assertThat(arg).isInstanceOf(Literal.class);
      Literal lit = (Literal) arg;
      assertThat(lit.getValue()).isEqualTo(42);
    }

    @Test
    void givenInvalidFunctionArgumentJson_whenDeserialize_thenThrowException() {
      String json = "{\"unknownField\":\"value\"}";

      assertThatThrownBy(() -> objectMapper.readValue(json, FunctionArgument.class))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Unknown FunctionArgument type");
    }
  }

  @Nested
  class ConditionDeserializerTests {
    @Test
    void givenSimpleConditionWithFieldRef_whenDeserialize_thenReturnConditionWithFieldReference() throws Exception {
      String json = "{\"propertyName\":\"startDate\",\"operator\":\"LESSER_THAN\",\"fieldRef\":\"endDate\"}";

      Condition condition = objectMapper.readValue(json, Condition.class);

      assertThat(condition).isInstanceOf(SimpleCondition.class);
      SimpleCondition simpleCondition = (SimpleCondition) condition;
      assertThat(simpleCondition.getPropertyName()).isEqualTo("startDate");
      assertThat(simpleCondition.getOperator()).isEqualTo(Operator.LESSER_THAN);
      assertThat(simpleCondition.isFieldReference()).isTrue();
      assertThat(simpleCondition.getFieldReference().getFieldName()).isEqualTo("endDate");
    }

    @Test
    void givenSimpleConditionWithFieldRefAndEquals_whenDeserialize_thenReturnConditionWithFieldReference() throws Exception {
      String json = "{\"propertyName\":\"field1\",\"operator\":\"EQUALS\",\"fieldRef\":\"field2\"}";

      Condition condition = objectMapper.readValue(json, Condition.class);

      assertThat(condition).isInstanceOf(SimpleCondition.class);
      SimpleCondition simpleCondition = (SimpleCondition) condition;
      assertThat(simpleCondition.getPropertyName()).isEqualTo("field1");
      assertThat(simpleCondition.getOperator()).isEqualTo(Operator.EQUALS);
      assertThat(simpleCondition.isFieldReference()).isTrue();
      assertThat(simpleCondition.getFieldReference().getFieldName()).isEqualTo("field2");
    }

    @Test
    void givenSimpleConditionWithFieldRefAndNotEquals_whenDeserialize_thenReturnConditionWithFieldReference() throws Exception {
      String json = "{\"propertyName\":\"field1\",\"operator\":\"NOT_EQUALS\",\"fieldRef\":\"field2\"}";

      Condition condition = objectMapper.readValue(json, Condition.class);

      SimpleCondition simpleCondition = (SimpleCondition) condition;
      assertThat(simpleCondition.getOperator()).isEqualTo(Operator.NOT_EQUALS);
      assertThat(simpleCondition.isFieldReference()).isTrue();
    }

    @Test
    void givenSimpleConditionWithFieldRefAndGreaterThan_whenDeserialize_thenReturnConditionWithFieldReference() throws Exception {
      String json = "{\"propertyName\":\"price\",\"operator\":\"GREATER_THAN\",\"fieldRef\":\"minPrice\"}";

      Condition condition = objectMapper.readValue(json, Condition.class);

      SimpleCondition simpleCondition = (SimpleCondition) condition;
      assertThat(simpleCondition.getOperator()).isEqualTo(Operator.GREATER_THAN);
      assertThat(simpleCondition.isFieldReference()).isTrue();
      assertThat(simpleCondition.getFieldReference().getFieldName()).isEqualTo("minPrice");
    }

    @Test
    void givenSimpleConditionWithFieldRefAndGreaterThanEquals_whenDeserialize_thenReturnConditionWithFieldReference() throws Exception {
      String json = "{\"propertyName\":\"price\",\"operator\":\"GREATER_THAN_EQUALS\",\"fieldRef\":\"minPrice\"}";

      Condition condition = objectMapper.readValue(json, Condition.class);

      SimpleCondition simpleCondition = (SimpleCondition) condition;
      assertThat(simpleCondition.getOperator()).isEqualTo(Operator.GREATER_THAN_EQUALS);
      assertThat(simpleCondition.isFieldReference()).isTrue();
    }

    @Test
    void givenSimpleConditionWithFieldRefAndLesserThanEquals_whenDeserialize_thenReturnConditionWithFieldReference() throws Exception {
      String json = "{\"propertyName\":\"price\",\"operator\":\"LESSER_THAN_EQUALS\",\"fieldRef\":\"maxPrice\"}";

      Condition condition = objectMapper.readValue(json, Condition.class);

      SimpleCondition simpleCondition = (SimpleCondition) condition;
      assertThat(simpleCondition.getOperator()).isEqualTo(Operator.LESSER_THAN_EQUALS);
      assertThat(simpleCondition.isFieldReference()).isTrue();
    }

    @Test
    void givenSimpleConditionWithValue_whenDeserialize_thenReturnConditionWithValue() throws Exception {
      String json = "{\"propertyName\":\"lastName\",\"operator\":\"EQUALS\",\"value\":\"Skywalker\"}";

      Condition condition = objectMapper.readValue(json, Condition.class);

      assertThat(condition).isInstanceOf(SimpleCondition.class);
      SimpleCondition simpleCondition = (SimpleCondition) condition;
      assertThat(simpleCondition.getPropertyName()).isEqualTo("lastName");
      assertThat(simpleCondition.getOperator()).isEqualTo(Operator.EQUALS);
      assertThat(simpleCondition.isFieldReference()).isFalse();
      assertThat(simpleCondition.getValue()).isEqualTo("Skywalker");
    }

    @Test
    void givenAndConditionWithFieldRef_whenDeserialize_thenReturnAndCondition() throws Exception {
      String json = "{\"and\":[{\"propertyName\":\"startDate\",\"operator\":\"LESSER_THAN\",\"fieldRef\":\"endDate\"},{\"propertyName\":\"status\",\"operator\":\"EQUALS\",\"value\":\"ACTIVE\"}]}";

      Condition condition = objectMapper.readValue(json, Condition.class);

      assertThat(condition).isInstanceOf(AndConditionsWrapper.class);
      AndConditionsWrapper andCondition = (AndConditionsWrapper) condition;
      assertThat(andCondition.getConditions()).hasSize(2);

      SimpleCondition fieldRefCondition = (SimpleCondition) andCondition.getConditions().get(0);
      assertThat(fieldRefCondition.isFieldReference()).isTrue();
      assertThat(fieldRefCondition.getFieldReference().getFieldName()).isEqualTo("endDate");

      SimpleCondition valueCondition = (SimpleCondition) andCondition.getConditions().get(1);
      assertThat(valueCondition.isFieldReference()).isFalse();
      assertThat(valueCondition.getValue()).isEqualTo("ACTIVE");
    }

    @Test
    void givenOrConditionWithFieldRef_whenDeserialize_thenReturnOrCondition() throws Exception {
      String json = "{\"or\":[{\"propertyName\":\"field1\",\"operator\":\"EQUALS\",\"fieldRef\":\"field2\"},{\"propertyName\":\"field3\",\"operator\":\"NOT_EQUALS\",\"fieldRef\":\"field4\"}]}";

      Condition condition = objectMapper.readValue(json, Condition.class);

      assertThat(condition).isInstanceOf(OrConditionsWrapper.class);
      OrConditionsWrapper orCondition = (OrConditionsWrapper) condition;
      assertThat(orCondition.getConditions()).hasSize(2);

      SimpleCondition condition1 = (SimpleCondition) orCondition.getConditions().get(0);
      assertThat(condition1.isFieldReference()).isTrue();

      SimpleCondition condition2 = (SimpleCondition) orCondition.getConditions().get(1);
      assertThat(condition2.isFieldReference()).isTrue();
    }

    @Test
    void givenNotConditionWithFieldRef_whenDeserialize_thenReturnNotCondition() throws Exception {
      String json = "{\"not\":{\"propertyName\":\"startDate\",\"operator\":\"GREATER_THAN\",\"fieldRef\":\"endDate\"}}";

      Condition condition = objectMapper.readValue(json, Condition.class);

      assertThat(condition).isInstanceOf(NotCondition.class);
      NotCondition notCondition = (NotCondition) condition;

      SimpleCondition innerCondition = (SimpleCondition) notCondition.getCondition();
      assertThat(innerCondition.isFieldReference()).isTrue();
      assertThat(innerCondition.getFieldReference().getFieldName()).isEqualTo("endDate");
    }

    @Test
    void givenFieldRefWithUnsupportedOperator_whenDeserialize_thenThrowException() {
      String json = "{\"propertyName\":\"field1\",\"operator\":\"IN\",\"fieldRef\":\"field2\"}";

      assertThatThrownBy(() -> objectMapper.readValue(json, Condition.class))
          .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void givenNestedFieldRef_whenDeserialize_thenReturnConditionWithNestedFieldReference() throws Exception {
      String json = "{\"propertyName\":\"order.startDate\",\"operator\":\"LESSER_THAN\",\"fieldRef\":\"order.endDate\"}";

      Condition condition = objectMapper.readValue(json, Condition.class);

      SimpleCondition simpleCondition = (SimpleCondition) condition;
      assertThat(simpleCondition.getPropertyName()).isEqualTo("order.startDate");
      assertThat(simpleCondition.isFieldReference()).isTrue();
      assertThat(simpleCondition.getFieldReference().getFieldName()).isEqualTo("order.endDate");
    }
  }
}
