package io.github.queritylib.querity.spring.web.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.github.queritylib.querity.api.SimpleSort;
import io.github.queritylib.querity.api.Sort;

import java.io.IOException;

import static io.github.queritylib.querity.spring.web.jackson.JsonFields.DIRECTION;
import static io.github.queritylib.querity.spring.web.jackson.JsonFields.PROPERTY_NAME;

public class SortDeserializer extends StdDeserializer<Sort> {

  protected SortDeserializer(JavaType valueType) {
    super(valueType);
  }

  @Override
  public Sort deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
    JsonNode root = jsonParser.readValueAsTree();
    return parseSort(root);
  }

  private static Sort parseSort(JsonNode jsonNode) {
    if (isSimpleSort(jsonNode)) {
      return parseSimpleSort(jsonNode);
    }
    throw new IllegalArgumentException("Unknown sort type: " + jsonNode);
  }

  private static boolean isSimpleSort(JsonNode jsonNode) {
    return jsonNode.hasNonNull(PROPERTY_NAME);
  }

  private static SimpleSort parseSimpleSort(JsonNode jsonNode) {
    SimpleSort.SimpleSortBuilder builder = SimpleSort.builder();

    builder.propertyName(jsonNode.get(PROPERTY_NAME).asText());

    if (jsonNode.hasNonNull(DIRECTION)) {
      builder.direction(SimpleSort.Direction.valueOf(jsonNode.get(DIRECTION).asText()));
    }

    return builder.build();
  }
}
