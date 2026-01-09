package io.github.queritylib.querity.spring.web.jackson;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.Deserializers;
import io.github.queritylib.querity.api.Condition;
import io.github.queritylib.querity.api.FunctionArgument;
import io.github.queritylib.querity.api.GroupBy;
import io.github.queritylib.querity.api.PropertyExpression;
import io.github.queritylib.querity.api.Select;
import io.github.queritylib.querity.api.Sort;

public class QuerityModule extends com.fasterxml.jackson.databind.Module {
  @Override
  public String getModuleName() {
    return getClass().getSimpleName();
  }

  @Override
  public Version version() {
    return Version.unknownVersion();
  }

  @Override
  public void setupModule(SetupContext setupContext) {
    setupContext.addDeserializers(new Deserializers.Base() {
      @Override
      public JsonDeserializer<?> findBeanDeserializer(JavaType type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
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
        return super.findBeanDeserializer(type, config, beanDesc);
      }
    });
  }
}
