package io.github.queritylib.querity.spring.data.jpa;

import io.github.queritylib.querity.api.Querity;
import io.github.queritylib.querity.jpa.QuerityJpaImpl;
import jakarta.persistence.EntityManager;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnProperty(prefix = "querity.autoconfigure", name = "enabled", matchIfMissing = true)
public class QuerityJpaAutoConfiguration {
  @Bean
  public Querity querity(EntityManager entityManager) {
    return new QuerityJpaImpl(entityManager);
  }
}
