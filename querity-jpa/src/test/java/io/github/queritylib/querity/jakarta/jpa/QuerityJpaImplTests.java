package io.github.queritylib.querity.jakarta.jpa;

import io.github.queritylib.querity.api.Querity;
import io.github.queritylib.querity.api.Query;
import io.github.queritylib.querity.jakarta.jpa.domain.Person;
import io.github.queritylib.querity.test.DatabaseSeeder;
import io.github.queritylib.querity.test.QuerityGenericTestSuite;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static io.github.queritylib.querity.api.Querity.filterByNative;
import static io.github.queritylib.querity.api.Querity.not;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(JpaTestExtension.class)
public abstract class QuerityJpaImplTests extends QuerityGenericTestSuite<Person, Long> {

  private static DatabaseSeeder<Person> databaseSeeder;
  private static Querity querity;
  private static EntityManager em;

  @Override
  protected DatabaseSeeder<Person> getDatabaseSeeder() {
    return databaseSeeder;
  }

  @Override
  protected Querity getQuerity() {
    return querity;
  }

  @BeforeAll
  static void setUpClass() {
    em = JpaTestExtension.getEntityManagerFactory().createEntityManager();
    querity = new QuerityJpaImpl(em);
    databaseSeeder = new JpaDatabaseSeeder(em);
    try {
      databaseSeeder.afterPropertiesSet();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @AfterAll
  static void tearDownClass() {
    em.close();
  }

  @Override
  protected Class<Person> getEntityClass() {
    return Person.class;
  }

  @Test
  void givenJpaNativeCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
    Query query = Querity.query()
        .filter(filterByNative(Specification.<Person>where(
            (root, cq, cb) ->
                cb.equal(root.get("lastName"), entity1.getLastName()))))
        .build();
    List<Person> result = querity.findAll(getEntityClass(), query);
    assertThat(result)
        .isNotEmpty()
        .containsExactlyInAnyOrderElementsOf(entities.stream()
            .filter(p -> entity1.getLastName().equals(p.getLastName()))
            .toList());
  }

  @Test
  void givenNotConditionWrappingJpaNativeCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
    Query query = Querity.query()
        .filter(not(filterByNative(Specification.<Person>where(
            (root, cq, cb) -> cb.and(
                root.get("lastName").isNotNull(),
                cb.equal(root.get("lastName"), entity1.getLastName())
            )))))
        .build();
    List<Person> result = querity.findAll(getEntityClass(), query);
    assertThat(result)
        .isNotEmpty()
        .containsExactlyInAnyOrderElementsOf(entities.stream()
            .filter(p -> !entity1.getLastName().equals(p.getLastName()))
            .toList());
  }
}
