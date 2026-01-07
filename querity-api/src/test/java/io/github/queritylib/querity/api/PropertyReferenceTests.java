package io.github.queritylib.querity.api;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PropertyReferenceTests {

  @Test
  void givenPropertyName_whenOf_thenReturnPropertyReference() {
    PropertyReference ref = PropertyReference.of("lastName");

    assertThat(ref.getPropertyName()).isEqualTo("lastName");
  }

  @Test
  void givenNestedPropertyName_whenOf_thenReturnPropertyReference() {
    PropertyReference ref = PropertyReference.of("address.city.name");

    assertThat(ref.getPropertyName()).isEqualTo("address.city.name");
  }

  @Test
  void givenPropertyReference_whenToExpressionString_thenReturnPropertyName() {
    PropertyReference ref = PropertyReference.of("firstName");

    assertThat(ref.toExpressionString()).isEqualTo("firstName");
  }

  @Test
  void givenPropertyReference_whenAs_thenReturnAliasedReference() {
    PropertyReference ref = PropertyReference.of("name").as("displayName");

    assertThat(ref.getPropertyName()).isEqualTo("name");
    assertThat(ref.getAlias()).isEqualTo("displayName");
    assertThat(ref.hasAlias()).isTrue();
  }

  @Test
  void givenTwoEqualPropertyReferences_whenEquals_thenReturnTrue() {
    PropertyReference ref1 = PropertyReference.of("name");
    PropertyReference ref2 = PropertyReference.of("name");

    assertThat(ref1)
        .isEqualTo(ref2)
        .hasSameHashCodeAs(ref2);
  }

  @Test
  void givenDifferentPropertyReferences_whenEquals_thenReturnFalse() {
    PropertyReference ref1 = PropertyReference.of("firstName");
    PropertyReference ref2 = PropertyReference.of("lastName");

    assertThat(ref1).isNotEqualTo(ref2);
  }

  @Test
  void givenPropertyReference_whenToString_thenContainsPropertyName() {
    PropertyReference ref = PropertyReference.of("testProperty");

    assertThat(ref.toString()).contains("testProperty");
  }

  @Test
  void givenNullPropertyName_whenOf_thenThrowException() {
    assertThrows(NullPointerException.class, () -> PropertyReference.of(null));
  }

  @Test
  void givenPropertyReference_whenIsPropertyExpression_thenReturnTrue() {
    PropertyReference ref = PropertyReference.of("name");

    assertThat(ref).isInstanceOf(PropertyExpression.class);
  }
}
