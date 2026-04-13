package io.github.queritylib.querity.spring.web.jackson;

import io.github.queritylib.querity.api.PropertyExpression;
import io.github.queritylib.querity.api.Select;
import io.github.queritylib.querity.api.SimpleSelect;
import lombok.SneakyThrows;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.deser.std.StdDeserializer;

import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

import static io.github.queritylib.querity.spring.web.jackson.JsonFields.EXPRESSIONS;
import static io.github.queritylib.querity.spring.web.jackson.JsonFields.PROPERTY_NAMES;

public class SelectDeserializer extends StdDeserializer<Select> {

  protected SelectDeserializer(JavaType valueType) {
    super(valueType);
  }

  @Override
  public Select deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws JacksonException {
    JsonNode root = jsonParser.readValueAsTree();
    return parseSelect(root, deserializationContext);
  }

  private static Select parseSelect(JsonNode jsonNode, DeserializationContext context) {
    if (jsonNode.hasNonNull(EXPRESSIONS)) {
      return parseSelectWithExpressions(jsonNode, context);
    }
    if (jsonNode.hasNonNull(PROPERTY_NAMES)) {
      return parseSimpleSelect(jsonNode);
    }
    throw new IllegalArgumentException("Unknown select type: " + jsonNode);
  }

  private static SimpleSelect parseSimpleSelect(JsonNode jsonNode) {
    JsonNode propertyNamesNode = jsonNode.get(PROPERTY_NAMES);
    List<String> propertyNames = StreamSupport.stream(
            Spliterators.spliteratorUnknownSize(propertyNamesNode.iterator(), Spliterator.ORDERED),
            false)
        .map(JsonNode::asString)
        .toList();
    return SimpleSelect.builder()
        .propertyNames(propertyNames)
        .build();
  }

  @SneakyThrows
  private static SimpleSelect parseSelectWithExpressions(JsonNode jsonNode, DeserializationContext context) {
    JsonNode expressionsNode = jsonNode.get(EXPRESSIONS);
    List<PropertyExpression> expressions = StreamSupport.stream(
            Spliterators.spliteratorUnknownSize(expressionsNode.iterator(), Spliterator.ORDERED),
            false)
        .map(node -> deserializeExpression(node, context))
        .toList();
    return SimpleSelect.builder()
        .expressions(expressions)
        .build();
  }

  @SneakyThrows
  private static PropertyExpression deserializeExpression(JsonNode node, DeserializationContext context) {
    return context.readTreeAsValue(node, PropertyExpression.class);
  }
}
