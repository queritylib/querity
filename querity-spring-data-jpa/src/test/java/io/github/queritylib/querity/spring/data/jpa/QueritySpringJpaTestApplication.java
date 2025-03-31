package io.github.queritylib.querity.spring.data.jpa;

import io.github.queritylib.querity.jpa.domain.Person;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackageClasses = Person.class)
public class QueritySpringJpaTestApplication {
  public static void main(String[] args) {
    SpringApplication.run(QueritySpringJpaTestApplication.class, args);
  }
}
