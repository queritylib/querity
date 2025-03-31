package io.github.queritylib.querity.spring.data.jpa;

import org.springframework.test.context.TestPropertySource;

import java.util.Comparator;

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
}
