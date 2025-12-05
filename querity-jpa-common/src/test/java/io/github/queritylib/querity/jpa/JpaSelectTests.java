package io.github.queritylib.querity.jpa;

import io.github.queritylib.querity.api.Select;
import io.github.queritylib.querity.api.SimpleSelect;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.github.queritylib.querity.api.Querity.selectBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JpaSelectTests {

  @Nested
  class FactoryMethodTests {
    @Test
    void givenSimpleSelect_whenOf_thenReturnJpaSimpleSelect() {
      SimpleSelect simpleSelect = selectBy("id", "name");

      JpaSelect jpaSelect = JpaSelect.of(simpleSelect);

      assertThat(jpaSelect).isInstanceOf(JpaSimpleSelect.class);
    }

    @Test
    void givenUnsupportedSelect_whenOf_thenThrowException() {
      Select unsupportedSelect = new UnsupportedSelect();

      assertThatThrownBy(() -> JpaSelect.of(unsupportedSelect))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Unsupported select type");
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

  // Helper class to test unsupported Select case
  private static class UnsupportedSelect implements Select {
    @Override
    public List<String> getPropertyNames() {
      return List.of();
    }
  }
}
