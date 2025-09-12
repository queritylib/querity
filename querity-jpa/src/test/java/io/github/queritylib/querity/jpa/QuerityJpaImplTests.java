package io.github.queritylib.querity.jpa;

import io.github.queritylib.querity.api.Querity;
import io.github.queritylib.querity.api.Query;
import io.github.queritylib.querity.jpa.domain.Person;
import io.github.queritylib.querity.test.DatabaseSeeder;
import io.github.queritylib.querity.test.QuerityGenericTestSuite;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.github.queritylib.querity.api.Querity.filterByNative;
import static io.github.queritylib.querity.api.Querity.not;
import static org.assertj.core.api.Assertions.assertThat;

public abstract class QuerityJpaImplTests extends QuerityGenericTestSuite<Person, Long> {

  private static EntityManagerFactory emf;
  private static EntityManager em;
  private static Querity querity;
  private static DatabaseSeeder<Person> databaseSeeder;

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
    emf = Persistence.createEntityManagerFactory("example");
    em = emf.createEntityManager();
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
    emf.close();
  }

  @Override
  protected Class<Person> getEntityClass() {
    return Person.class;
  }

  @Test
  void givenJpaNativeCondition_whenFindAll_thenReturnOnlyFilteredElements() {
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
  void givenNotConditionWrappingJpaNativeCondition_whenFindAll_thenReturnOnlyFilteredElements() {
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
