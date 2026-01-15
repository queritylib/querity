package io.github.queritylib.querity.spring.web.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.github.queritylib.querity.api.*;
import lombok.SneakyThrows;

import java.io.IOException;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static io.github.queritylib.querity.spring.web.jackson.JsonFields.FUNCTION;
import static io.github.queritylib.querity.spring.web.jackson.JsonFields.LEFT_EXPRESSION;

public class ConditionDeserializer extends StdDeserializer<Condition> {

  public static final String FIELD_CONDITIONS_WRAPPER_AND_CONDITIONS = "and";
  public static final String FIELD_CONDITIONS_WRAPPER_OR_CONDITIONS = "or";
  public static final String FIELD_SIMPLE_CONDITION_PROPERTY_NAME = "propertyName";
  public static final String FIELD_SIMPLE_CONDITION_OPERATOR = "operator";
  public static final String FIELD_SIMPLE_CONDITION_VALUE = "value";
  public static final String FIELD_SIMPLE_CONDITION_FIELD_REF = "fieldRef";
  public static final String FIELD_NOT_CONDITION_CONDITION = "not";

  protected ConditionDeserializer(JavaType valueType) {
    super(valueType);
  }

  @Override
  public Condition deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
    JsonNode root = jsonParser.readValueAsTree();
    return parseCondition(root, jsonParser, deserializationContext);
  }

  @SneakyThrows
  private static Condition parseCondition(JsonNode jsonNode, JsonParser jsonParser, DeserializationContext context) {
    if (isAndConditionsWrapper(jsonNode)) return parseAndConditionsWrapper(jsonNode, jsonParser, context);
    if (isOrConditionsWrapper(jsonNode)) return parseOrConditionsWrapper(jsonNode, jsonParser, context);
    if (isNotCondition(jsonNode)) return parseNotCondition(jsonNode, jsonParser, context);
    return parseSimpleCondition(jsonNode, context);
  }

  private static boolean isAndConditionsWrapper(JsonNode jsonNode) {
    return jsonNode.hasNonNull(FIELD_CONDITIONS_WRAPPER_AND_CONDITIONS);
  }

  private static boolean isOrConditionsWrapper(JsonNode jsonNode) {
    return jsonNode.hasNonNull(FIELD_CONDITIONS_WRAPPER_OR_CONDITIONS);
  }

  private static AndConditionsWrapper parseAndConditionsWrapper(JsonNode jsonNode, JsonParser jsonParser, DeserializationContext context) {
    return AndConditionsWrapper.builder()
        .conditions(parseConditions(jsonParser, jsonNode.get(FIELD_CONDITIONS_WRAPPER_AND_CONDITIONS), context))
        .build();
  }

  private static OrConditionsWrapper parseOrConditionsWrapper(JsonNode jsonNode, JsonParser jsonParser, DeserializationContext context) {
    return OrConditionsWrapper.builder()
        .conditions(parseConditions(jsonParser, jsonNode.get(FIELD_CONDITIONS_WRAPPER_OR_CONDITIONS), context))
        .build();
  }

  private static List<Condition> parseConditions(JsonParser jsonParser, JsonNode conditionsJsonNode, DeserializationContext context) {
    return StreamSupport.stream(
            Spliterators.spliteratorUnknownSize(conditionsJsonNode.elements(), Spliterator.ORDERED),
            false)
        .map(n -> parseCondition(n, jsonParser, context))
        .collect(Collectors.toList());
  }

  private static boolean isNotCondition(JsonNode jsonNode) {
    return jsonNode.hasNonNull(FIELD_NOT_CONDITION_CONDITION);
  }

  private static NotCondition parseNotCondition(JsonNode jsonNode, JsonParser jsonParser, DeserializationContext context) {
    return NotCondition.builder()
        .condition(parseCondition(jsonNode.get(FIELD_NOT_CONDITION_CONDITION), jsonParser, context))
        .build();
  }

  @SneakyThrows
  private static SimpleCondition parseSimpleCondition(JsonNode jsonNode, DeserializationContext context) {
    SimpleCondition.SimpleConditionBuilder builder = SimpleCondition.builder();
    
    // Check for leftExpression (function-based condition) or function shorthand
    if (jsonNode.hasNonNull(LEFT_EXPRESSION)) {
      PropertyExpression leftExpr = context.readTreeAsValue(jsonNode.get(LEFT_EXPRESSION), PropertyExpression.class);
      builder = builder.leftExpression(leftExpr);
    } else if (jsonNode.hasNonNull(FUNCTION)) {
      // Shorthand: function directly in condition object
      PropertyExpression leftExpr = context.readTreeAsValue(jsonNode, PropertyExpression.class);
      builder = builder.leftExpression(leftExpr);
    } else {
      builder = setIfNotNull(jsonNode, builder, FIELD_SIMPLE_CONDITION_PROPERTY_NAME, JsonNode::asText, builder::propertyName);
    }
    
    builder = setIfNotNull(jsonNode, builder, FIELD_SIMPLE_CONDITION_OPERATOR, node -> Operator.valueOf(node.asText()), builder::operator);

    // Check if this is a field reference
    if (jsonNode.hasNonNull(FIELD_SIMPLE_CONDITION_FIELD_REF)) {
      String fieldName = jsonNode.get(FIELD_SIMPLE_CONDITION_FIELD_REF).asText();
      builder = builder.value(FieldReference.of(fieldName));
    } else if (isArray(jsonNode, FIELD_SIMPLE_CONDITION_VALUE)) {
      builder = setArrayIfNotNull(jsonNode, builder, FIELD_SIMPLE_CONDITION_VALUE, JsonNode::asText, builder::value);
    } else {
      builder = setIfNotNull(jsonNode, builder, FIELD_SIMPLE_CONDITION_VALUE, ConditionDeserializer::extractValue, builder::value);
    }

    try {
      return builder.build();
    } catch (Exception ex) {
      throw new IllegalArgumentException(ex.getMessage(), ex);
    }
  }

  private static Object extractValue(JsonNode node) {
    if (node.isNumber()) {
      if (node.isInt()) return node.asInt();
      if (node.isLong()) return node.asLong();
      return node.asDouble();
    }
    return node.asText();
  }

  private static boolean isArray(JsonNode jsonNode, String fieldName) {
    return jsonNode.hasNonNull(fieldName) && jsonNode.get(fieldName).isArray();
  }

  private static <T> SimpleCondition.SimpleConditionBuilder setIfNotNull(JsonNode jsonNode, SimpleCondition.SimpleConditionBuilder builder, String fieldName, Function<JsonNode, T> valueProvider, Function<T, SimpleCondition.SimpleConditionBuilder> setValueFunction) {
    if (jsonNode.hasNonNull(fieldName))
      builder = setValueFunction.apply(valueProvider.apply(jsonNode.get(fieldName)));
    return builder;
  }

  @SuppressWarnings("unchecked")
  private static <T> SimpleCondition.SimpleConditionBuilder setArrayIfNotNull(JsonNode jsonNode, SimpleCondition.SimpleConditionBuilder builder, String fieldName, Function<JsonNode, T> valueProvider, Function<Object, SimpleCondition.SimpleConditionBuilder> setValueFunction) {
    if (jsonNode.hasNonNull(fieldName)) {
      JsonNode valueNode = jsonNode.get(fieldName);
      T[] values = (T[]) StreamSupport.stream(
              Spliterators.spliteratorUnknownSize(valueNode.elements(), Spliterator.ORDERED),
              false)
          .map(valueProvider)
          .toArray();
      builder = setValueFunction.apply(values);
    }
    return builder;
  }
}
