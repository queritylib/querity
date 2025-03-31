package io.github.queritylib.querity.jpa;

import io.github.queritylib.querity.jpa.domain.Person;
import io.github.queritylib.querity.test.QuerityGenericSpringTestSuite;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = QueritySpringJpaTestApplication.class)
public abstract class QuerityJpaImplTests extends QuerityGenericSpringTestSuite<Person, Long> {
  @Override
  protected Class<Person> getEntityClass() {
    return Person.class;
  }
}
