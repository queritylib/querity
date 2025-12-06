package io.github.queritylib.querity.jpa;

import io.github.queritylib.querity.api.Querity;
import io.github.queritylib.querity.api.Query;
import io.github.queritylib.querity.jpa.domain.Person;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

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
}
