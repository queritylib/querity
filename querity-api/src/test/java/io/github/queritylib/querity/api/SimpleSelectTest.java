package io.github.queritylib.querity.api;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static io.github.queritylib.querity.api.Querity.selectBy;
import static org.assertj.core.api.Assertions.assertThat;

class SimpleSelectTest {

  @Nested
  class CreationTests {
    @Test
    void givenPropertyNames_whenSelectBy_thenReturnSimpleSelect() {
      SimpleSelect select = selectBy("id", "name", "email");

      assertThat(select).isNotNull();
      assertThat(select.getPropertyNames()).containsExactly("id", "name", "email");
    }

    @Test
    void givenSinglePropertyName_whenSelectBy_thenReturnSimpleSelect() {
      SimpleSelect select = selectBy("id");

      assertThat(select).isNotNull();
      assertThat(select.getPropertyNames()).containsExactly("id");
    }

    @Test
    void givenPropertyNames_whenOf_thenReturnSimpleSelect() {
      SimpleSelect select = SimpleSelect.of("firstName", "lastName");

      assertThat(select).isNotNull();
      assertThat(select.getPropertyNames()).containsExactly("firstName", "lastName");
    }
  }

  @Nested
  class EqualsAndHashCodeTests {
    @Test
    void givenTwoSelectsWithSameProperties_whenEquals_thenReturnTrue() {
      SimpleSelect select1 = selectBy("id", "name");
      SimpleSelect select2 = selectBy("id", "name");

      assertThat(select1)
          .isEqualTo(select2)
          .hasSameHashCodeAs(select2);
    }

    @Test
    void givenTwoSelectsWithDifferentProperties_whenEquals_thenReturnFalse() {
      SimpleSelect select1 = selectBy("id", "name");
      SimpleSelect select2 = selectBy("id", "email");

      assertThat(select1).isNotEqualTo(select2);
    }

    @Test
    void givenTwoSelectsWithSamePropertiesDifferentOrder_whenEquals_thenReturnFalse() {
      SimpleSelect select1 = selectBy("id", "name");
      SimpleSelect select2 = selectBy("name", "id");

      assertThat(select1).isNotEqualTo(select2);
    }
  }

  @Nested
  class SelectInterfaceTests {
    @Test
    void givenSimpleSelect_whenCheckInterface_thenImplementsSelect() {
      SimpleSelect select = selectBy("id");

      assertThat(select).isInstanceOf(Select.class);
    }
  }

  @Nested
  class BuilderTests {
    @Test
    void givenSimpleSelect_whenToBuilder_thenCanModify() {
      SimpleSelect original = selectBy("id", "name");
      SimpleSelect modified = original.toBuilder()
          .clearPropertyNames()
          .propertyNames(Arrays.asList("email", "phone"))
          .build();

      assertThat(original.getPropertyNames()).containsExactly("id", "name");
      assertThat(modified.getPropertyNames()).containsExactly("email", "phone");
    }

    @Test
    void givenSimpleSelect_whenToBuilderAndAddProperty_thenPropertyIsAdded() {
      SimpleSelect original = selectBy("id");
      SimpleSelect modified = original.toBuilder()
          .propertyName("name")
          .build();

      assertThat(original.getPropertyNames()).containsExactly("id");
      assertThat(modified.getPropertyNames()).containsExactly("id", "name");
    }
  }

  @Nested
  class UsageInQueryTests {
    @Test
    void givenSimpleSelect_whenUsedInQuery_thenQueryHasSelect() {
      SimpleSelect select = selectBy("id", "name", "email");
      Query query = Querity.query()
          .select(select)
          .build();

      assertThat(query.hasSelect()).isTrue();
      assertThat(query.getSelect()).isEqualTo(select);
    }

    @Test
    void givenPropertyNames_whenUsedInQueryBuilder_thenQueryHasSimpleSelect() {
      Query query = Querity.query()
          .selectBy("id", "name")
          .build();

      assertThat(query.hasSelect()).isTrue();
      assertThat(query.getSelect()).isInstanceOf(SimpleSelect.class);
      assertThat(query.getSelect().getPropertyNames()).containsExactly("id", "name");
    }

    @Test
    void givenNoSelect_whenQuery_thenQueryHasNoSelect() {
      Query query = Querity.query().build();

      assertThat(query.hasSelect()).isFalse();
      assertThat(query.getSelect()).isNull();
    }
  }

  @Nested
  class ToStringTests {
    @Test
    void givenSimpleSelect_whenToString_thenContainsPropertyNames() {
      SimpleSelect select = selectBy("id", "name");

      assertThat(select.toString()).contains("id", "name");
    }
  }
}
