package io.github.queritylib.querity.spring.web;

import tools.jackson.databind.JacksonModule;
import io.github.queritylib.querity.spring.web.jackson.QuerityModule;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import(QuerityWebMvcSupport.class)
@EnableAspectJAutoProxy
@ConditionalOnProperty(prefix = "querity.web.autoconfigure", name = "enabled", matchIfMissing = true)
public class QueritySpringWebAutoConfiguration {
  @Bean
  public JacksonModule querityJacksonModule() {
    return new QuerityModule();
  }

  @Bean
  QuerityPreprocessorAspect querityPreprocessorAspect(
      ApplicationContext applicationContext) {
    return new QuerityPreprocessorAspect(applicationContext);
  }
}
