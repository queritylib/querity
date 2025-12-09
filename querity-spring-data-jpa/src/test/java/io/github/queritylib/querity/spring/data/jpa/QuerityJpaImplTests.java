package io.github.queritylib.querity.spring.data.jpa;

import io.github.queritylib.querity.api.Operator;
import io.github.queritylib.querity.api.Querity;
import io.github.queritylib.querity.api.Query;
import io.github.queritylib.querity.jpa.AliasedSelectionSpecification;
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
    Query query = Querity.query()
        .filter(filterBy("firstName", Operator.IS_NOT_NULL))
        .filter(filterBy("lastName", Operator.IS_NOT_NULL))
        .select(selectByNative(fullNameSpec))
        .build();
    List<Map<String, Object>> result = querity.findAllProjected(Person.class, query);
    assertThat(result).isNotEmpty();
    assertThat(result).allSatisfy(map -> {
      assertThat(map).containsKey("fullName");
      assertThat(map.get("fullName")).isInstanceOf(String.class);
    });
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
    Query query = Querity.query()
        .filter(filterBy("lastName", entity1.getLastName()))
        .select(selectByNative(idSpec, fullNameSpec))
        .build();
    List<Map<String, Object>> result = querity.findAllProjected(Person.class, query);
    assertThat(result).isNotEmpty();
    assertThat(result).allSatisfy(map -> {
      assertThat(map).containsKey("id");
      assertThat(map).containsKey("fullName");
      String fullName = (String) map.get("fullName");
      assertThat(fullName).endsWith(entity1.getLastName());
    });
  }
}

