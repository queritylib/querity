package io.github.queritylib.querity.jpa;

import io.github.queritylib.querity.api.Querity;
import io.github.queritylib.querity.jpa.domain.Person;
import jakarta.persistence.EntityManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EntityScan(basePackageClasses = Person.class)
public class QueritySpringJpaTestApplication {
  public static void main(String[] args) {
    SpringApplication.run(QueritySpringJpaTestApplication.class, args);
  }

  @Bean
  public Querity querity(EntityManager entityManager) {
    return new QuerityJpaImpl(entityManager);
  }
}
