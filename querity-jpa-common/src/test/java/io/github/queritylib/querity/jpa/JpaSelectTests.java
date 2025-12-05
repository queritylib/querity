package io.github.queritylib.querity.jpa;

import io.github.queritylib.querity.api.SimpleSelect;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.github.queritylib.querity.api.Querity.selectBy;
import static org.assertj.core.api.Assertions.assertThat;

class JpaSelectTests {

  @Nested
  class FactoryMethodTests {
    @Test
    void givenSimpleSelect_whenOf_thenReturnJpaSimpleSelect() {
      SimpleSelect simpleSelect = selectBy("id", "name");

      JpaSelect jpaSelect = JpaSelect.of(simpleSelect);

      assertThat(jpaSelect).isInstanceOf(JpaSimpleSelect.class);
    }
  }

  @Nested
  class JpaSimpleSelectTests {
    @Test
    void givenSimpleSelect_whenGetPropertyNames_thenReturnPropertyNames() {
      SimpleSelect simpleSelect = selectBy("id", "name", "email");
      JpaSimpleSelect jpaSimpleSelect = new JpaSimpleSelect(simpleSelect);

      assertThat(jpaSimpleSelect.getPropertyNames()).containsExactly("id", "name", "email");
    }

    @Test
    void givenSimpleSelectWithNestedProperty_whenGetPropertyNames_thenReturnNestedPropertyNames() {
      SimpleSelect simpleSelect = selectBy("id", "address.city", "address.street");
      JpaSimpleSelect jpaSimpleSelect = new JpaSimpleSelect(simpleSelect);

      assertThat(jpaSimpleSelect.getPropertyNames()).containsExactly("id", "address.city", "address.street");
    }
  }
}
