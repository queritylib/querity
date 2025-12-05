package io.github.queritylib.querity.spring.web.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.github.queritylib.querity.api.Select;
import io.github.queritylib.querity.api.SimpleSelect;

import java.io.IOException;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

public class SelectDeserializer extends StdDeserializer<Select> {

  public static final String FIELD_PROPERTY_NAMES = "propertyNames";

  protected SelectDeserializer(JavaType valueType) {
    super(valueType);
  }

  @Override
  public Select deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
    JsonNode root = jsonParser.readValueAsTree();
    return parseSelect(root);
  }

  private static Select parseSelect(JsonNode jsonNode) {
    if (isSimpleSelect(jsonNode)) {
      return parseSimpleSelect(jsonNode);
    }
    throw new IllegalArgumentException("Unknown select type: " + jsonNode);
  }

  private static boolean isSimpleSelect(JsonNode jsonNode) {
    return jsonNode.hasNonNull(FIELD_PROPERTY_NAMES);
  }

  private static SimpleSelect parseSimpleSelect(JsonNode jsonNode) {
    JsonNode propertyNamesNode = jsonNode.get(FIELD_PROPERTY_NAMES);
    List<String> propertyNames = StreamSupport.stream(
            Spliterators.spliteratorUnknownSize(propertyNamesNode.elements(), Spliterator.ORDERED),
            false)
        .map(JsonNode::asText)
        .toList();
    return SimpleSelect.builder()
        .propertyNames(propertyNames)
        .build();
  }
}
