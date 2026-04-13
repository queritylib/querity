package io.github.queritylib.querity.spring.web.jackson;

import io.github.queritylib.querity.api.Condition;
import io.github.queritylib.querity.api.FunctionArgument;
import io.github.queritylib.querity.api.GroupBy;
import io.github.queritylib.querity.api.PropertyExpression;
import io.github.queritylib.querity.api.Select;
import io.github.queritylib.querity.api.Sort;
import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.DeserializationConfig;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.Deserializers;

class QuerityDeserializers extends Deserializers.Base {
  @Override
  public ValueDeserializer<?> findBeanDeserializer(JavaType type, DeserializationConfig config, BeanDescription.Supplier beanDescSupplier) {
    Class<?> raw = type.getRawClass();
    if (Condition.class.isAssignableFrom(raw)) {
      return new ConditionDeserializer(type);
    }
    if (Select.class.isAssignableFrom(raw)) {
      return new SelectDeserializer(type);
    }
    if (Sort.class.isAssignableFrom(raw)) {
      return new SortDeserializer(type);
    }
    if (GroupBy.class.isAssignableFrom(raw)) {
      return new GroupByDeserializer(type);
    }
    // Use exact class match to avoid conflicts between PropertyExpression and FunctionArgument
    // (PropertyExpression extends FunctionArgument)
    if (raw == PropertyExpression.class) {
      return new PropertyExpressionDeserializer(type);
    }
    if (raw == FunctionArgument.class) {
      return new FunctionArgumentDeserializer(type);
    }
    return null;
  }

  @Override
  public boolean hasDeserializerFor(DeserializationConfig config, Class<?> valueType) {
    return Condition.class.isAssignableFrom(valueType)
        || Select.class.isAssignableFrom(valueType)
        || Sort.class.isAssignableFrom(valueType)
        || GroupBy.class.isAssignableFrom(valueType)
        || valueType == PropertyExpression.class
        || valueType == FunctionArgument.class;
  }
}
