package io.github.queritylib.querity.spring.web.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.github.queritylib.querity.api.GroupBy;
import io.github.queritylib.querity.api.PropertyExpression;
import io.github.queritylib.querity.api.SimpleGroupBy;
import lombok.SneakyThrows;

import java.io.IOException;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

import static io.github.queritylib.querity.spring.web.jackson.JsonFields.EXPRESSIONS;
import static io.github.queritylib.querity.spring.web.jackson.JsonFields.PROPERTY_NAMES;

public class GroupByDeserializer extends StdDeserializer<GroupBy> {

  protected GroupByDeserializer(JavaType valueType) {
    super(valueType);
  }

  @Override
  public GroupBy deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
    JsonNode root = jsonParser.readValueAsTree();
    return parseGroupBy(root, deserializationContext);
  }

  private static GroupBy parseGroupBy(JsonNode jsonNode, DeserializationContext context) {
    if (jsonNode.hasNonNull(EXPRESSIONS)) {
      return parseGroupByWithExpressions(jsonNode, context);
    }
    if (jsonNode.hasNonNull(PROPERTY_NAMES)) {
      return parseSimpleGroupBy(jsonNode);
    }
    throw new IllegalArgumentException("Unknown groupBy type: " + jsonNode);
  }

  private static SimpleGroupBy parseSimpleGroupBy(JsonNode jsonNode) {
    JsonNode propertyNamesNode = jsonNode.get(PROPERTY_NAMES);
    List<String> propertyNames = StreamSupport.stream(
            Spliterators.spliteratorUnknownSize(propertyNamesNode.elements(), Spliterator.ORDERED),
            false)
        .map(JsonNode::asText)
        .toList();
    return SimpleGroupBy.builder()
        .propertyNames(propertyNames)
        .build();
  }

  @SneakyThrows
  private static SimpleGroupBy parseGroupByWithExpressions(JsonNode jsonNode, DeserializationContext context) {
    JsonNode expressionsNode = jsonNode.get(EXPRESSIONS);
    List<PropertyExpression> expressions = StreamSupport.stream(
            Spliterators.spliteratorUnknownSize(expressionsNode.elements(), Spliterator.ORDERED),
            false)
        .map(node -> deserializeExpression(node, context))
        .toList();
    return SimpleGroupBy.builder()
        .expressions(expressions)
        .build();
  }

  @SneakyThrows
  private static PropertyExpression deserializeExpression(JsonNode node, DeserializationContext context) {
    return context.readTreeAsValue(node, PropertyExpression.class);
  }
}
