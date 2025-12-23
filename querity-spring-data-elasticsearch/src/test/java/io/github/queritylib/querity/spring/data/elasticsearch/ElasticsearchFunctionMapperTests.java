package io.github.queritylib.querity.spring.data.elasticsearch;

import io.github.queritylib.querity.api.Function;
import io.github.queritylib.querity.api.FunctionCall;
import io.github.queritylib.querity.api.PropertyExpression;
import io.github.queritylib.querity.api.PropertyReference;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ElasticsearchFunctionMapperTests {

  @ParameterizedTest
  @EnumSource(Function.class)
  void givenAnyFunction_whenIsSupported_thenReturnFalse(Function function) {
    boolean result = ElasticsearchFunctionMapper.isSupported(function);

    assertThat(result).isFalse();
  }

  @Test
  void givenPropertyReference_whenGetFieldName_thenReturnPropertyName() {
    PropertyReference propertyRef = PropertyReference.of("userName");

    String fieldName = ElasticsearchFunctionMapper.getFieldName(propertyRef);

    assertThat(fieldName).isEqualTo("userName");
  }

  @Test
  void givenNestedPropertyReference_whenGetFieldName_thenReturnNestedPropertyName() {
    PropertyReference propertyRef = PropertyReference.of("address.city");

    String fieldName = ElasticsearchFunctionMapper.getFieldName(propertyRef);

    assertThat(fieldName).isEqualTo("address.city");
  }

  @Test
  void givenFunctionCall_whenGetFieldName_thenThrowUnsupportedOperationException() {
    FunctionCall functionCall = FunctionCall.of(Function.UPPER, PropertyReference.of("name"));

    assertThatThrownBy(() -> ElasticsearchFunctionMapper.getFieldName(functionCall))
        .isInstanceOf(UnsupportedOperationException.class)
        .hasMessageContaining("Function UPPER is not supported in Elasticsearch");
  }

  @Test
  void givenUnsupportedExpressionType_whenGetFieldName_thenThrowIllegalArgumentException() {
    PropertyExpression unsupported = new UnsupportedPropertyExpression();

    assertThatThrownBy(() -> ElasticsearchFunctionMapper.getFieldName(unsupported))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Unsupported expression type");
  }

  @Test
  void givenPropertyReference_whenContainsFunction_thenReturnFalse() {
    PropertyReference propertyRef = PropertyReference.of("name");

    boolean result = ElasticsearchFunctionMapper.containsFunction(propertyRef);

    assertThat(result).isFalse();
  }

  @Test
  void givenFunctionCall_whenContainsFunction_thenReturnTrue() {
    FunctionCall functionCall = FunctionCall.of(Function.LOWER, PropertyReference.of("name"));

    boolean result = ElasticsearchFunctionMapper.containsFunction(functionCall);

    assertThat(result).isTrue();
  }

  @Test
  void givenPropertyReference_whenValidateNoFunctions_thenNoExceptionThrown() {
    PropertyReference propertyRef = PropertyReference.of("email");

    // Should not throw
    ElasticsearchFunctionMapper.validateNoFunctions(propertyRef);
  }

  @Test
  void givenFunctionCall_whenValidateNoFunctions_thenThrowUnsupportedOperationException() {
    FunctionCall functionCall = FunctionCall.of(Function.LENGTH, PropertyReference.of("description"));

    assertThatThrownBy(() -> ElasticsearchFunctionMapper.validateNoFunctions(functionCall))
        .isInstanceOf(UnsupportedOperationException.class)
        .hasMessageContaining("Function LENGTH is not supported in Elasticsearch");
  }

  private static class UnsupportedPropertyExpression implements PropertyExpression {
    @Override
    public String toExpressionString() {
      return "unsupported";
    }
  }
}
