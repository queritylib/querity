package io.github.queritylib.querity.spring.web.jackson;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.Deserializers;
import io.github.queritylib.querity.api.Condition;

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
        return super.findBeanDeserializer(type, config, beanDesc);
      }
    });
  }
}
