package io.github.queritylib.querity.spring.data.jpa;

import io.github.queritylib.querity.api.AdvancedQuery;
import io.github.queritylib.querity.api.Operator;
import io.github.queritylib.querity.api.Querity;
import io.github.queritylib.querity.api.Query;
import io.github.queritylib.querity.jpa.AliasedSelectionSpecification;
import io.github.queritylib.querity.jpa.JPAHints;
import io.github.queritylib.querity.jpa.OrderSpecification;
import io.github.queritylib.querity.jpa.SelectionSpecification;
import io.github.queritylib.querity.jpa.domain.Person;
import io.github.queritylib.querity.test.QuerityGenericSpringTestSuite;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static io.github.queritylib.querity.api.Querity.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = QueritySpringJpaTestApplication.class)
public abstract class QuerityJpaImplTests extends QuerityGenericSpringTestSuite<Person, Long> {

  @org.springframework.beans.factory.annotation.Autowired
  protected jakarta.persistence.EntityManager entityManager;

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

  @Test
  void givenNativeConditionWithSorting_whenFindAll_thenReturnFilteredAndSortedElements() {
    Specification<Person> specification = (root, cq, cb) -> root.get("lastName").isNotNull();
    Query query = Querity.query()
        .filter(filterByNative(specification))
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
        .filter(filterBy("lastName", Operator.IS_NOT_NULL))
        .sort(sortByNative(orderSpec))
        .build();
    List<Person> result = querity.findAll(getEntityClass(), query);
    Comparator<Person> comparator = getStringComparator(Person::getLastName)
        .thenComparing(Person::getId);
    assertThat(result)
        .isNotEmpty()
        .containsExactlyElementsOf(entities.stream()
            .filter(p -> p.getLastName() != null)
            .sorted(comparator)
            .toList());
  }

  @Test
  void givenNativeSortDesc_whenFindAll_thenReturnSortedElementsDescending() {
    OrderSpecification<Person> orderSpec = (root, cb) -> cb.desc(root.get("lastName"));
    Query query = Querity.query()
        .filter(filterBy("lastName", Operator.IS_NOT_NULL))
        .sort(sortByNative(orderSpec))
        .build();
    List<Person> result = querity.findAll(getEntityClass(), query);
    Comparator<Person> comparator = getStringComparator(Person::getLastName, true)
        .thenComparing(Person::getId);
    assertThat(result)
        .isNotEmpty()
        .containsExactlyElementsOf(entities.stream()
            .filter(p -> p.getLastName() != null)
            .sorted(comparator)
            .toList());
  }

  @Test
  void givenNativeSortWithNativeCondition_whenFindAll_thenReturnFilteredAndSortedElements() {
    Specification<Person> specification = (root, cq, cb) -> cb.equal(root.get("lastName"), entity1.getLastName());
    OrderSpecification<Person> orderSpec = (root, cb) -> cb.asc(root.get("firstName"));
    Query query = Querity.query()
        .filter(filterByNative(specification))
        .sort(sortByNative(orderSpec))
        .build();
    List<Person> result = querity.findAll(getEntityClass(), query);
    Comparator<Person> comparator = getStringComparator(Person::getFirstName)
        .thenComparing(Person::getId);
    assertThat(result)
        .isNotEmpty()
        .containsExactlyElementsOf(entities.stream()
            .filter(p -> entity1.getLastName().equals(p.getLastName()))
            .sorted(comparator)
            .toList());
  }

  @Test
  void givenMixedSortTypes_whenFindAll_thenReturnSortedElements() {
    OrderSpecification<Person> orderSpec = (root, cb) -> cb.asc(root.get("lastName"));
    Query query = Querity.query()
        .filter(filterBy("lastName", Operator.IS_NOT_NULL))
        .sort(sortByNative(orderSpec), sortBy("firstName"))
        .build();
    List<Person> result = querity.findAll(getEntityClass(), query);
    assertThat(result).isNotEmpty();
    Comparator<Person> comparator = getStringComparator(Person::getLastName)
        .thenComparing(Person::getFirstName)
        .thenComparing(Person::getId);
    assertThat(result)
        .isNotEmpty()
        .containsExactlyElementsOf(entities.stream()
            .filter(p -> p.getLastName() != null)
            .sorted(comparator)
            .toList());
  }

  @Test
  void givenNativeSortWithExpression_whenFindAll_thenReturnSortedByExpression() {
    // Sort by length of lastName (expression-based sorting)
    OrderSpecification<Person> orderSpec = (root, cb) -> cb.asc(cb.length(root.get("lastName")));
    Query query = Querity.query()
        .filter(filterBy("lastName", Operator.IS_NOT_NULL))
        .sort(sortByNative(orderSpec), sortBy("id"))
        .build();
    List<Person> result = querity.findAll(getEntityClass(), query);
    assertThat(result).isNotEmpty();
    // Verify results are sorted by lastName length ascending, then by id
    Comparator<Person> comparator = Comparator
        .comparingInt((Person p) -> p.getLastName().length())
        .thenComparing(Person::getId);
    assertThat(result)
        .containsExactlyElementsOf(entities.stream()
            .filter(p -> p.getLastName() != null)
            .sorted(comparator)
            .toList());
  }

  @Test
  void givenNativeSelectWithConcatExpression_whenFindAllProjected_thenReturnConcatenatedValues() {
    // Use coalesce to handle null values in concatenation
    SelectionSpecification<Person> fullNameSpec = AliasedSelectionSpecification.of(
        (root, cb) -> cb.concat(
            cb.concat(cb.coalesce(root.get("firstName"), ""), " "),
            cb.coalesce(root.get("lastName"), "")),
        "fullName"
    );
    AdvancedQuery query = Querity.advancedQuery()
        .filter(filterBy("firstName", Operator.IS_NOT_NULL))
        .filter(filterBy("lastName", Operator.IS_NOT_NULL))
        .select(selectByNative(fullNameSpec))
        .build();
    List<Map<String, Object>> result = querity.findAllProjected(Person.class, query);
    assertThat(result)
        .isNotEmpty()
        .allSatisfy(map -> assertThat(map)
            .containsKey("fullName")
            .extractingByKey("fullName")
            .isInstanceOf(String.class));
    // Verify the concatenation is correct for entity1
    String expectedFullName = entity1.getFirstName() + " " + entity1.getLastName();
    assertThat(result).anyMatch(map -> expectedFullName.equals(map.get("fullName")));
  }

  @Test
  void givenNativeSelectWithMultipleExpressionsAndFilter_whenFindAllProjected_thenReturnFilteredResults() {
    SelectionSpecification<Person> fullNameSpec = AliasedSelectionSpecification.of(
        (root, cb) -> cb.concat(
            cb.concat(cb.coalesce(root.get("firstName"), ""), " "),
            cb.coalesce(root.get("lastName"), "")),
        "fullName"
    );
    SelectionSpecification<Person> idSpec = AliasedSelectionSpecification.of(
        (root, cb) -> root.get("id"),
        "id"
    );
    AdvancedQuery query = Querity.advancedQuery()
        .filter(filterBy("lastName", entity1.getLastName()))
        .select(selectByNative(idSpec, fullNameSpec))
        .build();
    List<Map<String, Object>> result = querity.findAllProjected(Person.class, query);
    assertThat(result)
        .isNotEmpty()
        .allSatisfy(map -> {
          assertThat(map)
              .containsKey("id")
              .containsKey("fullName");
          String fullName = (String) map.get("fullName");
          assertThat(fullName).endsWith(entity1.getLastName());
        });
  }

  @Test
  void givenFetchJoinCustomizer_whenFindAll_thenEagerlyLoadAssociations() {
    Query query = Querity.query()
        .filter(filterBy("lastName", Operator.IS_NOT_NULL))
        .customize(JPAHints.fetchJoin("orders")) // orders is Lazy (OneToMany)
        .build();
    List<Person> result = querity.findAll(getEntityClass(), query);
    
    jakarta.persistence.PersistenceUnitUtil unitUtil = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();
    
    assertThat(result)
        .isNotEmpty()
        .allSatisfy(person -> {
          // Verify that associations are initialized implies fetch join worked
          // Without fetch join, orders would be uninitialized (Lazy)
          assertThat(unitUtil.isLoaded(person, "orders"))
              .as("Orders should be loaded eagerly due to fetchJoin")
              .isTrue();
        });
  }

  @Test
  void givenNestedFetchJoinCustomizer_whenFindAll_thenEagerlyLoadNestedAssociations() {
    // We cannot test "orders.items" due to Hibernate MultipleBagFetchException (2 lists)
    // So we test a safe nested path "orders.person" just to verify graph building works
    Query query = Querity.query()
        .filter(filterBy("lastName", Operator.IS_NOT_NULL))
        .customize(JPAHints.fetchJoin("orders", "orders.person"))
        .build();
    List<Person> result = querity.findAll(getEntityClass(), query);
    
    jakarta.persistence.PersistenceUnitUtil unitUtil = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();

    assertThat(result)
        .isNotEmpty()
        .allSatisfy(person -> {
          assertThat(unitUtil.isLoaded(person, "orders")).isTrue();
          person.getOrders().forEach(order -> 
              assertThat(unitUtil.isLoaded(order, "person"))
                  .as("Nested order.person should be loaded")
                  .isTrue()
          );
        });
  }

  @Test
  void givenNamedEntityGraphCustomizer_whenFindAll_thenEagerlyLoadAssociations() {
    Query query = Querity.query()
        .filter(filterBy("lastName", Operator.IS_NOT_NULL))
        .customize(JPAHints.namedEntityGraph("Person.withOrders"))
        .build();
    List<Person> result = querity.findAll(getEntityClass(), query);
    
    jakarta.persistence.PersistenceUnitUtil unitUtil = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();
    
    assertThat(result)
        .isNotEmpty()
        .allSatisfy(person -> {
          // Orders should be loaded by the named entity graph
          assertThat(unitUtil.isLoaded(person, "orders"))
              .as("Orders should be loaded eagerly due to NamedEntityGraph")
              .isTrue();
        });
  }

  @Test
  void givenMultipleCustomizers_whenFindAll_thenApplyAllCustomizers() {
    Query query = Querity.query()
        .filter(filterBy("lastName", entity1.getLastName()))
        .customize(JPAHints.fetchJoin("address"))
        .customize(JPAHints.cacheable(true))
        .customize(JPAHints.timeout(5000))
        .build();
    List<Person> result = querity.findAll(getEntityClass(), query);
    assertThat(result)
        .isNotEmpty()
        .containsExactlyInAnyOrderElementsOf(entities.stream()
            .filter(p -> entity1.getLastName().equals(p.getLastName()))
            .toList());
  }
}
