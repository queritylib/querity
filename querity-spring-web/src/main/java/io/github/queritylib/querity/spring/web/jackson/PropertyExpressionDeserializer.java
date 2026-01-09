package io.github.queritylib.querity.spring.web.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.github.queritylib.querity.api.FunctionCall;
import io.github.queritylib.querity.api.PropertyExpression;
import io.github.queritylib.querity.api.PropertyReference;

import java.io.IOException;

import static io.github.queritylib.querity.spring.web.jackson.JsonFields.FUNCTION;
import static io.github.queritylib.querity.spring.web.jackson.JsonFields.PROPERTY_NAME;

/**
 * Deserializer for {@link PropertyExpression} interface.
 * <p>
 * Determines the concrete type based on the JSON structure:
 * <ul>
 *   <li>If the JSON has a "propertyName" field, it's a {@link PropertyReference}</li>
 *   <li>If the JSON has a "function" field, it's a {@link FunctionCall}</li>
 * </ul>
 */
public class PropertyExpressionDeserializer extends StdDeserializer<PropertyExpression> {

  protected PropertyExpressionDeserializer(JavaType valueType) {
    super(valueType);
  }

  @Override
  public PropertyExpression deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
    JsonNode root = jsonParser.readValueAsTree();
    return parsePropertyExpression(root, context);
  }

  private static PropertyExpression parsePropertyExpression(JsonNode jsonNode, DeserializationContext context) throws IOException {
    if (jsonNode.hasNonNull(FUNCTION)) {
      return context.readTreeAsValue(jsonNode, FunctionCall.class);
    }
    if (jsonNode.hasNonNull(PROPERTY_NAME)) {
      return context.readTreeAsValue(jsonNode, PropertyReference.class);
    }
    throw new IllegalArgumentException("Unknown PropertyExpression type: " + jsonNode +
        ". Expected either 'propertyName' (for PropertyReference) or 'function' (for FunctionCall)");
  }
}
