package io.github.queritylib.querity.spring.data.mongodb;

import io.github.queritylib.querity.api.Function;
import io.github.queritylib.querity.api.FunctionCall;
import io.github.queritylib.querity.api.Literal;
import io.github.queritylib.querity.api.PropertyExpression;
import io.github.queritylib.querity.api.PropertyReference;
import org.bson.Document;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MongodbFunctionMapperTests {

  @ParameterizedTest
  @EnumSource(Function.class)
  void givenAnyFunction_whenIsSupported_thenReturnTrue(Function function) {
    boolean result = MongodbFunctionMapper.isSupported(function);

    assertThat(result).isTrue();
  }

  @Test
  void givenPropertyReference_whenToExpression_thenReturnFieldReference() {
    PropertyReference ref = PropertyReference.of("userName");

    Object result = MongodbFunctionMapper.toExpression(ref);

    assertThat(result).isEqualTo("$userName");
  }

  @Test
  void givenIdProperty_whenToExpression_thenReturnMongoIdReference() {
    PropertyReference ref = PropertyReference.of("id");

    Object result = MongodbFunctionMapper.toExpression(ref);

    assertThat(result).isEqualTo("$_id");
  }

  @Test
  void givenPropertyReference_whenGetFieldName_thenReturnFieldName() {
    PropertyReference ref = PropertyReference.of("email");

    String result = MongodbFunctionMapper.getFieldName(ref);

    assertThat(result).isEqualTo("email");
  }

  @Test
  void givenFunctionCall_whenGetFieldName_thenThrowUnsupportedOperationException() {
    FunctionCall fc = FunctionCall.of(Function.UPPER, PropertyReference.of("name"));

    assertThatThrownBy(() -> MongodbFunctionMapper.getFieldName(fc))
        .isInstanceOf(UnsupportedOperationException.class)
        .hasMessageContaining("aggregation pipeline");
  }

  @Test
  void givenPropertyReference_whenContainsFunction_thenReturnFalse() {
    assertThat(MongodbFunctionMapper.containsFunction(PropertyReference.of("name"))).isFalse();
  }

  @Test
  void givenFunctionCall_whenContainsFunction_thenReturnTrue() {
    FunctionCall fc = FunctionCall.of(Function.LOWER, PropertyReference.of("name"));
    assertThat(MongodbFunctionMapper.containsFunction(fc)).isTrue();
  }

  @Test
  void givenUnsupportedExpressionType_whenToExpression_thenThrowIllegalArgumentException() {
    PropertyExpression unsupported = new UnsupportedPropertyExpression();

    assertThatThrownBy(() -> MongodbFunctionMapper.toExpression(unsupported))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Unsupported expression type");
  }

  @Nested
  class ArithmeticFunctionTests {
    @Test
    void givenAbsFunction_whenToExpression_thenReturnAbsDocument() {
      FunctionCall fc = FunctionCall.of(Function.ABS, PropertyReference.of("value"));

      Object result = MongodbFunctionMapper.toExpression(fc);

      assertThat(result).isInstanceOf(Document.class);
      assertThat((Document) result).containsEntry("$abs", "$value");
    }

    @Test
    void givenSqrtFunction_whenToExpression_thenReturnSqrtDocument() {
      FunctionCall fc = FunctionCall.of(Function.SQRT, PropertyReference.of("value"));

      Object result = MongodbFunctionMapper.toExpression(fc);

      assertThat(result).isInstanceOf(Document.class);
      assertThat((Document) result).containsEntry("$sqrt", "$value");
    }

    @Test
    void givenModFunction_whenToExpression_thenReturnModDocument() {
      FunctionCall fc = FunctionCall.of(Function.MOD, PropertyReference.of("value"), Literal.of(10));

      Object result = MongodbFunctionMapper.toExpression(fc);

      assertThat(result).isInstanceOf(Document.class);
      assertThat(((Document) result).get("$mod")).isInstanceOf(List.class);
    }
  }

  @Nested
  class StringFunctionTests {
    @Test
    void givenUpperFunction_whenToExpression_thenReturnToUpperDocument() {
      FunctionCall fc = FunctionCall.of(Function.UPPER, PropertyReference.of("name"));

      Object result = MongodbFunctionMapper.toExpression(fc);

      assertThat(result).isInstanceOf(Document.class);
      assertThat((Document) result).containsEntry("$toUpper", "$name");
    }

    @Test
    void givenLowerFunction_whenToExpression_thenReturnToLowerDocument() {
      FunctionCall fc = FunctionCall.of(Function.LOWER, PropertyReference.of("name"));

      Object result = MongodbFunctionMapper.toExpression(fc);

      assertThat(result).isInstanceOf(Document.class);
      assertThat((Document) result).containsEntry("$toLower", "$name");
    }

    @Test
    void givenConcatFunction_whenToExpression_thenReturnConcatDocument() {
      FunctionCall fc = FunctionCall.of(Function.CONCAT, PropertyReference.of("firstName"), Literal.of(" "), PropertyReference.of("lastName"));

      Object result = MongodbFunctionMapper.toExpression(fc);

      assertThat(result).isInstanceOf(Document.class);
      assertThat(((Document) result).get("$concat")).isInstanceOf(List.class);
    }

    @Test
    void givenSubstringFunction_whenToExpression_thenReturnSubstrDocument() {
      FunctionCall fc = FunctionCall.of(Function.SUBSTRING, PropertyReference.of("text"), Literal.of(1), Literal.of(5));

      Object result = MongodbFunctionMapper.toExpression(fc);

      assertThat(result).isInstanceOf(Document.class);
      assertThat(((Document) result).get("$substr")).isInstanceOf(List.class);
    }

    @Test
    void givenTrimFunction_whenToExpression_thenReturnTrimDocument() {
      FunctionCall fc = FunctionCall.of(Function.TRIM, PropertyReference.of("text"));

      Object result = MongodbFunctionMapper.toExpression(fc);

      assertThat(result).isInstanceOf(Document.class);
      assertThat(((Document) result).get("$trim")).isInstanceOf(Document.class);
    }

    @Test
    void givenLtrimFunction_whenToExpression_thenReturnLtrimDocument() {
      FunctionCall fc = FunctionCall.of(Function.LTRIM, PropertyReference.of("text"));

      Object result = MongodbFunctionMapper.toExpression(fc);

      assertThat(result).isInstanceOf(Document.class);
      assertThat(((Document) result).get("$ltrim")).isInstanceOf(Document.class);
    }

    @Test
    void givenRtrimFunction_whenToExpression_thenReturnRtrimDocument() {
      FunctionCall fc = FunctionCall.of(Function.RTRIM, PropertyReference.of("text"));

      Object result = MongodbFunctionMapper.toExpression(fc);

      assertThat(result).isInstanceOf(Document.class);
      assertThat(((Document) result).get("$rtrim")).isInstanceOf(Document.class);
    }

    @Test
    void givenLengthFunction_whenToExpression_thenReturnCondDocument() {
      FunctionCall fc = FunctionCall.of(Function.LENGTH, PropertyReference.of("text"));

      Object result = MongodbFunctionMapper.toExpression(fc);

      assertThat(result).isInstanceOf(Document.class);
      assertThat(((Document) result).get("$cond")).isNotNull();
    }

    @Test
    void givenLocateFunction_whenToExpression_thenReturnAddDocument() {
      FunctionCall fc = FunctionCall.of(Function.LOCATE, Literal.of("@"), PropertyReference.of("email"));

      Object result = MongodbFunctionMapper.toExpression(fc);

      assertThat(result).isInstanceOf(Document.class);
      assertThat(((Document) result).get("$add")).isInstanceOf(List.class);
    }
  }

  @Nested
  class DateTimeFunctionTests {
    @Test
    void givenCurrentDateFunction_whenToExpression_thenReturnDateTruncDocument() {
      FunctionCall fc = FunctionCall.of(Function.CURRENT_DATE);

      Object result = MongodbFunctionMapper.toExpression(fc);

      assertThat(result).isInstanceOf(Document.class);
      assertThat(((Document) result).get("$dateTrunc")).isNotNull();
    }

    @Test
    void givenCurrentTimeFunction_whenToExpression_thenReturnNowVariable() {
      FunctionCall fc = FunctionCall.of(Function.CURRENT_TIME);

      Object result = MongodbFunctionMapper.toExpression(fc);

      assertThat(result).isEqualTo("$$NOW");
    }

    @Test
    void givenCurrentTimestampFunction_whenToExpression_thenReturnNowVariable() {
      FunctionCall fc = FunctionCall.of(Function.CURRENT_TIMESTAMP);

      Object result = MongodbFunctionMapper.toExpression(fc);

      assertThat(result).isEqualTo("$$NOW");
    }
  }

  @Nested
  class ConditionalFunctionTests {
    @Test
    void givenCoalesceWithOneArg_whenToExpression_thenReturnSimpleExpression() {
      FunctionCall fc = FunctionCall.of(Function.COALESCE, PropertyReference.of("name"));

      Object result = MongodbFunctionMapper.toExpression(fc);

      assertThat(result).isEqualTo("$name");
    }

    @Test
    void givenCoalesceWithMultipleArgs_whenToExpression_thenReturnNestedIfNull() {
      FunctionCall fc = FunctionCall.of(Function.COALESCE, PropertyReference.of("nickname"), PropertyReference.of("firstName"), Literal.of("Anonymous"));

      Object result = MongodbFunctionMapper.toExpression(fc);

      assertThat(result).isInstanceOf(Document.class);
      assertThat(((Document) result).get("$ifNull")).isNotNull();
    }

    @Test
    void givenNullifFunction_whenToExpression_thenReturnCondDocument() {
      FunctionCall fc = FunctionCall.of(Function.NULLIF, PropertyReference.of("status"), Literal.of("INACTIVE"));

      Object result = MongodbFunctionMapper.toExpression(fc);

      assertThat(result).isInstanceOf(Document.class);
      assertThat(((Document) result).get("$cond")).isNotNull();
    }
  }

  @Nested
  class AggregateFunctionTests {
    @Test
    void givenCountFunction_whenToExpression_thenReturnSumDocument() {
      FunctionCall fc = FunctionCall.of(Function.COUNT, PropertyReference.of("id"));

      Object result = MongodbFunctionMapper.toExpression(fc);

      assertThat(result).isInstanceOf(Document.class);
      assertThat((Document) result).containsEntry("$sum", 1);
    }

    @Test
    void givenSumFunction_whenToExpression_thenReturnSumDocument() {
      FunctionCall fc = FunctionCall.of(Function.SUM, PropertyReference.of("amount"));

      Object result = MongodbFunctionMapper.toExpression(fc);

      assertThat(result).isInstanceOf(Document.class);
      assertThat((Document) result).containsEntry("$sum", "$amount");
    }

    @Test
    void givenAvgFunction_whenToExpression_thenReturnAvgDocument() {
      FunctionCall fc = FunctionCall.of(Function.AVG, PropertyReference.of("price"));

      Object result = MongodbFunctionMapper.toExpression(fc);

      assertThat(result).isInstanceOf(Document.class);
      assertThat((Document) result).containsEntry("$avg", "$price");
    }

    @Test
    void givenMinFunction_whenToExpression_thenReturnMinDocument() {
      FunctionCall fc = FunctionCall.of(Function.MIN, PropertyReference.of("value"));

      Object result = MongodbFunctionMapper.toExpression(fc);

      assertThat(result).isInstanceOf(Document.class);
      assertThat((Document) result).containsEntry("$min", "$value");
    }

    @Test
    void givenMaxFunction_whenToExpression_thenReturnMaxDocument() {
      FunctionCall fc = FunctionCall.of(Function.MAX, PropertyReference.of("value"));

      Object result = MongodbFunctionMapper.toExpression(fc);

      assertThat(result).isInstanceOf(Document.class);
      assertThat((Document) result).containsEntry("$max", "$value");
    }
  }

  @Nested
  class NestedFunctionTests {
    @Test
    void givenNestedFunctions_whenToExpression_thenReturnNestedDocument() {
      // UPPER(TRIM(name))
      FunctionCall trim = FunctionCall.of(Function.TRIM, PropertyReference.of("name"));
      FunctionCall upper = FunctionCall.of(Function.UPPER, trim);

      Object result = MongodbFunctionMapper.toExpression(upper);

      assertThat(result).isInstanceOf(Document.class);
      Document upperDoc = (Document) result;
      assertThat(upperDoc.get("$toUpper")).isInstanceOf(Document.class);
    }
  }

  @Nested
  class LiteralTests {
    @Test
    void givenStringLiteral_whenUsedAsArgument_thenPreserved() {
      FunctionCall fc = FunctionCall.of(Function.CONCAT, Literal.of("Hello"), Literal.of(" "), Literal.of("World"));

      Object result = MongodbFunctionMapper.toExpression(fc);

      assertThat(result).isInstanceOf(Document.class);
      @SuppressWarnings("unchecked")
      List<Object> args = (List<Object>) ((Document) result).get("$concat");
      assertThat(args).containsExactly("Hello", " ", "World");
    }

    @Test
    void givenNumberLiteral_whenUsedAsArgument_thenPreserved() {
      FunctionCall fc = FunctionCall.of(Function.ABS, Literal.of(-42));

      Object result = MongodbFunctionMapper.toExpression(fc);

      assertThat(result).isInstanceOf(Document.class);
      assertThat((Document) result).containsEntry("$abs", -42);
    }

    @Test
    void givenBooleanLiteral_whenUsedAsArgument_thenPreserved() {
      FunctionCall fc = FunctionCall.of(Function.COALESCE, PropertyReference.of("active"), Literal.of(true));

      Object result = MongodbFunctionMapper.toExpression(fc);

      assertThat(result).isInstanceOf(Document.class);
    }
  }

  private static class UnsupportedPropertyExpression implements PropertyExpression {
    @Override
    public String toExpressionString() {
      return "unsupported";
    }
  }
}
