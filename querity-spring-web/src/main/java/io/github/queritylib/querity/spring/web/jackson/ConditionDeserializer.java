package io.github.queritylib.querity.spring.web.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.github.queritylib.querity.api.*;

import java.io.IOException;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ConditionDeserializer extends StdDeserializer<Condition> {

  public static final String FIELD_CONDITIONS_WRAPPER_AND_CONDITIONS = "and";
  public static final String FIELD_CONDITIONS_WRAPPER_OR_CONDITIONS = "or";
  public static final String FIELD_SIMPLE_CONDITION_PROPERTY_NAME = "propertyName";
  public static final String FIELD_SIMPLE_CONDITION_OPERATOR = "operator";
  public static final String FIELD_SIMPLE_CONDITION_VALUE = "value";
  public static final String FIELD_NOT_CONDITION_CONDITION = "not";

  protected ConditionDeserializer(JavaType valueType) {
    super(valueType);
  }

  @Override
  public Condition deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
    JsonNode root = jsonParser.readValueAsTree();
    return parseCondition(root, jsonParser);
  }

  private static Condition parseCondition(JsonNode jsonNode, JsonParser jsonParser) {
    JsonParser jp = jsonNode.traverse();
    jp.setCodec(jsonParser.getCodec());
    if (isAndConditionsWrapper(jsonNode)) return parseAndConditionsWrapper(jsonNode, jsonParser);
    if (isOrConditionsWrapper(jsonNode)) return parseOrConditionsWrapper(jsonNode, jsonParser);
    if (isNotCondition(jsonNode)) return parseNotCondition(jsonNode, jsonParser);
    return parseSimpleCondition(jsonNode);
  }

  private static boolean isAndConditionsWrapper(JsonNode jsonNode) {
    return jsonNode.hasNonNull(FIELD_CONDITIONS_WRAPPER_AND_CONDITIONS);
  }

  private static boolean isOrConditionsWrapper(JsonNode jsonNode) {
    return jsonNode.hasNonNull(FIELD_CONDITIONS_WRAPPER_OR_CONDITIONS);
  }

  private static AndConditionsWrapper parseAndConditionsWrapper(JsonNode jsonNode, JsonParser jsonParser) {
    return AndConditionsWrapper.builder()
        .conditions(parseConditions(jsonParser, jsonNode.get(FIELD_CONDITIONS_WRAPPER_AND_CONDITIONS)))
        .build();
  }

  private static OrConditionsWrapper parseOrConditionsWrapper(JsonNode jsonNode, JsonParser jsonParser) {
    return OrConditionsWrapper.builder()
        .conditions(parseConditions(jsonParser, jsonNode.get(FIELD_CONDITIONS_WRAPPER_OR_CONDITIONS)))
        .build();
  }

  private static List<Condition> parseConditions(JsonParser jsonParser, JsonNode conditionsJsonNode) {
    return StreamSupport.stream(
            Spliterators.spliteratorUnknownSize(conditionsJsonNode.elements(), Spliterator.ORDERED),
            false)
        .map(n -> parseCondition(n, jsonParser))
        .collect(Collectors.toList());
  }

  private static boolean isNotCondition(JsonNode jsonNode) {
    return jsonNode.hasNonNull(FIELD_NOT_CONDITION_CONDITION);
  }

  private static NotCondition parseNotCondition(JsonNode jsonNode, JsonParser jsonParser) {
    return NotCondition.builder()
        .condition(parseCondition(jsonNode.get(FIELD_NOT_CONDITION_CONDITION), jsonParser))
        .build();
  }

  private static SimpleCondition parseSimpleCondition(JsonNode jsonNode) {
    SimpleCondition.SimpleConditionBuilder builder = SimpleCondition.builder();
    builder = setIfNotNull(jsonNode, builder, FIELD_SIMPLE_CONDITION_PROPERTY_NAME, JsonNode::asText, builder::propertyName);
    builder = setIfNotNull(jsonNode, builder, FIELD_SIMPLE_CONDITION_OPERATOR, node -> Operator.valueOf(node.asText()), builder::operator);
    builder = setIfNotNull(jsonNode, builder, FIELD_SIMPLE_CONDITION_VALUE, JsonNode::asText, builder::value);
    try {
      return builder.build();
    } catch (Exception ex) {
      throw new IllegalArgumentException(ex.getMessage(), ex);
    }
  }

  private static <T> SimpleCondition.SimpleConditionBuilder setIfNotNull(JsonNode jsonNode, SimpleCondition.SimpleConditionBuilder builder, String fieldName, Function<JsonNode, T> valueProvider, Function<T, SimpleCondition.SimpleConditionBuilder> setValueFunction) {
    if (jsonNode.hasNonNull(fieldName))
      builder = setValueFunction.apply(valueProvider.apply(jsonNode.get(fieldName)));
    return builder;
  }
}
