package io.github.queritylib.querity.spring.web.jackson;

import tools.jackson.core.Version;
import tools.jackson.databind.JacksonModule;

public class QuerityModule extends JacksonModule {
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
    setupContext.addDeserializers(new QuerityDeserializers());
  }
}
