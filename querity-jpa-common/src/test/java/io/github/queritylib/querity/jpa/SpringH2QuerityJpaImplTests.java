package io.github.queritylib.querity.jpa;

import io.github.queritylib.querity.api.Querity;
import io.github.queritylib.querity.api.Query;
import io.github.queritylib.querity.jpa.domain.Person;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static io.github.queritylib.querity.api.Querity.selectByNative;
import static org.assertj.core.api.Assertions.assertThat;

@TestPropertySource(properties = {
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.properties.hibernate.globally_quoted_identifiers=true"
})
class SpringH2QuerityJpaImplTests extends QuerityJpaImplTests {

  /**
   * Overridden because sort behaves differently in H2 regarding null values
   */
  @Override
  protected <C extends Comparable<? super C>> Comparator<C> getSortComparator(boolean reversed) {
    Comparator<C> comparator = Comparator.nullsFirst(Comparator.naturalOrder());
    if (reversed) comparator = comparator.reversed();
    return comparator;
  }

  @Test
  void givenSelectWithProjection_whenFindAllProjected_thenReturnOnlySelectedFields() {
    Query query = Querity.query()
        .selectBy("firstName", "lastName")
        .build();
    List<Map<String, Object>> result = querity.findAllProjected(Person.class, query);
    assertThat(result).isNotEmpty();
    assertThat(result).allSatisfy(map -> {
      assertThat(map).containsKey("firstName");
    });
  }

  @Test
  void givenQueryWithFilterAndProjection_whenFindAllProjected_thenReturnFilteredAndProjectedResults() {
    String lastName = ((Person) entity1).getLastName();
    Query query = Querity.query()
        .filter(Querity.filterBy("lastName", lastName))
        .selectBy("firstName", "lastName")
        .build();
    List<Map<String, Object>> result = querity.findAllProjected(Person.class, query);
    assertThat(result).isNotEmpty();
    assertThat(result).allSatisfy(map -> {
      assertThat(map.get("lastName")).isEqualTo(lastName);
    });
  }

  @Test
  void givenQueryWithNestedFieldProjection_whenFindAllProjected_thenReturnNestedFieldValues() {
    Query query = Querity.query()
        .selectBy("firstName", "address.city")
        .build();
    List<Map<String, Object>> result = querity.findAllProjected(Person.class, query);
    assertThat(result).isNotEmpty();
    assertThat(result).allSatisfy(map -> {
      assertThat(map).containsKey("firstName");
      assertThat(map).containsKey("city");
    });
  }

  @Test
  void givenNativeSelectWithSimpleField_whenFindAllProjected_thenReturnFieldValues() {
    SelectionSpecification<Person> firstNameSpec = AliasedSelectionSpecification.of(
        (root, cb) -> root.get("firstName"),
        "firstName"
    );
    Query query = Querity.query()
        .select(selectByNative(firstNameSpec))
        .build();
    List<Map<String, Object>> result = querity.findAllProjected(Person.class, query);
    assertThat(result).isNotEmpty();
    assertThat(result).allSatisfy(map -> {
      assertThat(map).containsKey("firstName");
    });
    // Verify the value is correct for entity1
    assertThat(result).anyMatch(map -> entity1.getFirstName().equals(map.get("firstName")));
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
        .filter(Querity.filterBy("firstName", io.github.queritylib.querity.api.Operator.IS_NOT_NULL))
        .filter(Querity.filterBy("lastName", io.github.queritylib.querity.api.Operator.IS_NOT_NULL))
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
        .filter(Querity.filterBy("lastName", entity1.getLastName()))
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
