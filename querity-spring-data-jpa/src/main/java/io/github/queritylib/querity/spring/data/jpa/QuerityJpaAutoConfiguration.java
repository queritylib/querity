package io.github.queritylib.querity.spring.data.jpa;

import io.github.queritylib.querity.api.Querity;
import jakarta.persistence.EntityManager;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class QuerityJpaAutoConfiguration {
  @Bean
  public Querity querity(EntityManager entityManager) {
    return new QuerityJpaImpl(entityManager);
  }
}
