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

import java.util.Comparator;
import java.util.List;

import static io.github.queritylib.querity.api.Querity.filterByNative;
import static io.github.queritylib.querity.api.Querity.not;
import static io.github.queritylib.querity.api.Querity.sortBy;
import static io.github.queritylib.querity.api.Querity.sortByNative;
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

  @Test
  void givenNativeConditionWithSorting_whenFindAll_thenReturnFilteredAndSortedElements() {
    Query query = Querity.query()
        .filter(filterByNative(Specification.<Person>where(
            (root, cq, cb) -> root.get("lastName").isNotNull())))
        .sort(sortBy("id"))
        .build();
    List<Person> result = querity.findAll(getEntityClass(), query);
    assertThat(result).isNotEmpty();
    assertThat(result)
        .extracting(Person::getId)
        .isSorted();
  }

  @Test
  void givenNativeSortAsc_whenFindAll_thenReturnSortedElements() {
    OrderSpecification<Person> orderSpec = (root, cb) -> cb.asc(root.get("lastName"));
    Query query = Querity.query()
        .sort(sortByNative(orderSpec))
        .build();
    List<Person> result = querity.findAll(getEntityClass(), query);
    assertThat(result).isNotEmpty();
    // In H2, nulls are sorted first by default for ASC
    assertThat(result)
        .extracting(Person::getLastName)
        .isSortedAccordingTo(Comparator.nullsFirst(Comparator.naturalOrder()));
  }

  @Test
  void givenNativeSortDesc_whenFindAll_thenReturnSortedElementsDescending() {
    OrderSpecification<Person> orderSpec = (root, cb) -> cb.desc(root.get("lastName"));
    Query query = Querity.query()
        .sort(sortByNative(orderSpec))
        .build();
    List<Person> result = querity.findAll(getEntityClass(), query);
    assertThat(result).isNotEmpty();
    // In H2, nulls are sorted last for DESC
    assertThat(result)
        .extracting(Person::getLastName)
        .isSortedAccordingTo(Comparator.nullsLast(Comparator.<String>reverseOrder()));
  }

  @Test
  void givenNativeSortWithNativeCondition_whenFindAll_thenReturnFilteredAndSortedElements() {
    OrderSpecification<Person> orderSpec = (root, cb) -> cb.asc(root.get("firstName"));
    Query query = Querity.query()
        .filter(filterByNative(Specification.<Person>where(
            (root, cq, cb) -> cb.equal(root.get("lastName"), entity1.getLastName()))))
        .sort(sortByNative(orderSpec))
        .build();
    List<Person> result = querity.findAll(getEntityClass(), query);
    assertThat(result)
        .isNotEmpty()
        .extracting(Person::getFirstName)
        .isSorted();
    assertThat(result)
        .allMatch(p -> entity1.getLastName().equals(p.getLastName()));
  }

  @Test
  void givenMixedSortTypes_whenFindAll_thenReturnSortedElements() {
    OrderSpecification<Person> orderSpec = (root, cb) -> cb.asc(root.get("lastName"));
    Query query = Querity.query()
        .sort(sortByNative(orderSpec), sortBy("firstName"))
        .build();
    List<Person> result = querity.findAll(getEntityClass(), query);
    assertThat(result).isNotEmpty();
    // Verifica che i risultati siano ordinati prima per lastName, poi per firstName
    // In H2, nulls are sorted first by default
    assertThat(result)
        .isSortedAccordingTo(Comparator
            .comparing(Person::getLastName, Comparator.nullsFirst(Comparator.naturalOrder()))
            .thenComparing(Person::getFirstName, Comparator.nullsFirst(Comparator.naturalOrder())));
  }
}
