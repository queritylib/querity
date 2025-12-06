package io.github.queritylib.querity.spring.web.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.github.queritylib.querity.api.SimpleSort;
import io.github.queritylib.querity.api.Sort;

import java.io.IOException;

public class SortDeserializer extends StdDeserializer<Sort> {

  public static final String FIELD_PROPERTY_NAME = "propertyName";
  public static final String FIELD_DIRECTION = "direction";

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
    return jsonNode.hasNonNull(FIELD_PROPERTY_NAME);
  }

  private static SimpleSort parseSimpleSort(JsonNode jsonNode) {
    SimpleSort.SimpleSortBuilder builder = SimpleSort.builder();
    
    builder.propertyName(jsonNode.get(FIELD_PROPERTY_NAME).asText());
    
    if (jsonNode.hasNonNull(FIELD_DIRECTION)) {
      builder.direction(SimpleSort.Direction.valueOf(jsonNode.get(FIELD_DIRECTION).asText()));
    }
    
    return builder.build();
  }
}
