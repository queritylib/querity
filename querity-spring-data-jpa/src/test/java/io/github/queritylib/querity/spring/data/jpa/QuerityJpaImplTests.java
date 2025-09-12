package io.github.queritylib.querity.spring.data.jpa;

import io.github.queritylib.querity.api.Querity;
import io.github.queritylib.querity.api.Query;
import io.github.queritylib.querity.jpa.domain.Person;
import io.github.queritylib.querity.test.QuerityGenericSpringTestSuite;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static io.github.queritylib.querity.api.Querity.filterByNative;
import static io.github.queritylib.querity.api.Querity.not;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = QueritySpringJpaTestApplication.class)
public abstract class QuerityJpaImplTests extends QuerityGenericSpringTestSuite<Person, Long> {
  @Override
  protected Class<Person> getEntityClass() {
    return Person.class;
  }

  @Test
  void givenJpaNativeCondition_whenFindAll_thenReturnOnlyFilteredElements() {
    Specification<Person> specification = (root, cq, cb) -> cb.equal(root.get("lastName"), entity1.getLastName());
    Query query = Querity.query()
        .filter(filterByNative(specification))
        .build();
    List<Person> result = querity.findAll(getEntityClass(), query);
    assertThat(result)
        .isNotEmpty()
        .containsExactlyInAnyOrderElementsOf(entities.stream()
            .filter(p -> entity1.getLastName().equals(p.getLastName()))
            .toList());
  }

  @Test
  void givenNotConditionWrappingJpaNativeCondition_whenFindAll_thenReturnOnlyFilteredElements() {
    Specification<Person> specification = (root, cq, cb) -> cb.and(
        root.get("lastName").isNotNull(),
        cb.equal(root.get("lastName"), entity1.getLastName())
    );
    Query query = Querity.query()
        .filter(not(filterByNative(specification)))
        .build();
    List<Person> result = querity.findAll(getEntityClass(), query);
    assertThat(result)
        .isNotEmpty()
        .containsExactlyInAnyOrderElementsOf(entities.stream()
            .filter(p -> !entity1.getLastName().equals(p.getLastName()))
            .toList());
  }
}
