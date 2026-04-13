package io.github.queritylib.querity.spring.web.jackson;

import io.github.queritylib.querity.api.FunctionArgument;
import io.github.queritylib.querity.api.FunctionCall;
import io.github.queritylib.querity.api.Literal;
import io.github.queritylib.querity.api.PropertyReference;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.deser.std.StdDeserializer;

import static io.github.queritylib.querity.spring.web.jackson.JsonFields.*;

/**
 * Deserializer for {@link FunctionArgument} interface.
 * <p>
 * Determines the concrete type based on the JSON structure:
 * <ul>
 *   <li>If the JSON has a "propertyName" field, it's a {@link PropertyReference}</li>
 *   <li>If the JSON has a "function" field, it's a {@link FunctionCall}</li>
 *   <li>If the JSON has a "value" field, it's a {@link Literal}</li>
 * </ul>
 */
public class FunctionArgumentDeserializer extends StdDeserializer<FunctionArgument> {

  protected FunctionArgumentDeserializer(JavaType valueType) {
    super(valueType);
  }

  @Override
  public FunctionArgument deserialize(JsonParser jsonParser, DeserializationContext context) throws JacksonException {
    JsonNode root = jsonParser.readValueAsTree();
    return parseFunctionArgument(root, context);
  }

  private static FunctionArgument parseFunctionArgument(JsonNode jsonNode, DeserializationContext context) {
    if (jsonNode.hasNonNull(FUNCTION)) {
      return context.readTreeAsValue(jsonNode, FunctionCall.class);
    }
    if (jsonNode.hasNonNull(PROPERTY_NAME)) {
      return context.readTreeAsValue(jsonNode, PropertyReference.class);
    }
    if (jsonNode.hasNonNull(VALUE)) {
      return context.readTreeAsValue(jsonNode, Literal.class);
    }
    throw new IllegalArgumentException("Unknown FunctionArgument type: " + jsonNode +
        ". Expected 'propertyName' (PropertyReference), 'function' (FunctionCall), or 'value' (Literal)");
  }
}
