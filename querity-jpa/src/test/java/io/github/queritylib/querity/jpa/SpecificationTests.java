package io.github.queritylib.querity.jpa;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SpecificationTests {

  @Test
  void givenNullSpec_whenWhere_thenReturnSpecReturningNullPredicate() {
    Specification<Object> result = Specification.where(null);

    assertThat(result).isNotNull();
    assertThat(result.toPredicate(null, null, null)).isNull();
  }

  @Test
  void givenValidSpec_whenWhere_thenReturnSameSpec() {
    Specification<Object> spec = (root, cq, cb) -> null;

    Specification<Object> result = Specification.where(spec);

    assertThat(result).isSameAs(spec);
  }
}
