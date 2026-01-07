package io.github.queritylib.querity.api;

import org.junit.jupiter.api.Test;

import static io.github.queritylib.querity.api.SimpleSort.Direction.ASC;
import static io.github.queritylib.querity.api.SimpleSort.Direction.DESC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SimpleSortExpressionTests {

  @Test
  void givenPropertyName_whenBuild_thenReturnSimpleSortWithPropertyName() {
    SimpleSort sort = SimpleSort.builder()
        .propertyName("lastName")
        .build();

    assertThat(sort.getPropertyName()).isEqualTo("lastName");
    assertThat(sort.getDirection()).isEqualTo(ASC);
    assertThat(sort.hasExpression()).isFalse();
  }

  @Test
  void givenPropertyNameWithDirection_whenBuild_thenReturnSimpleSortWithDirection() {
    SimpleSort sort = SimpleSort.builder()
        .propertyName("age")
        .direction(DESC)
        .build();

    assertThat(sort.getPropertyName()).isEqualTo("age");
    assertThat(sort.getDirection()).isEqualTo(DESC);
  }

  @Test
  void givenExpression_whenBuild_thenReturnSimpleSortWithExpression() {
    FunctionCall expr = FunctionCall.of(Function.LENGTH, PropertyReference.of("name"));
    SimpleSort sort = SimpleSort.builder()
        .expression(expr)
        .build();

    assertThat(sort.hasExpression()).isTrue();
    assertThat(sort.getExpression()).isEqualTo(expr);
    assertThat(sort.getDirection()).isEqualTo(ASC);
  }

  @Test
  void givenExpressionWithDirection_whenBuild_thenReturnSimpleSortWithDirectionAndExpression() {
    FunctionCall expr = FunctionCall.of(Function.UPPER, PropertyReference.of("lastName"));
    SimpleSort sort = SimpleSort.builder()
        .expression(expr)
        .direction(DESC)
        .build();

    assertThat(sort.hasExpression()).isTrue();
    assertThat(sort.getExpression()).isEqualTo(expr);
    assertThat(sort.getDirection()).isEqualTo(DESC);
  }

  @Test
  void givenNoPropertyNameOrExpression_whenBuild_thenThrowException() {
    SimpleSort.SimpleSortBuilder builder = SimpleSort.builder();

    assertThrows(IllegalArgumentException.class, builder::build);
  }

  @Test
  void givenBothPropertyNameAndExpression_whenBuild_thenThrowException() {
    SimpleSort.SimpleSortBuilder builder = SimpleSort.builder()
        .propertyName("name")
        .expression(FunctionCall.of(Function.UPPER, PropertyReference.of("name")));

    assertThrows(IllegalArgumentException.class, builder::build);
  }

  @Test
  void givenPropertyName_whenGetEffectiveExpression_thenReturnPropertyReference() {
    SimpleSort sort = SimpleSort.builder()
        .propertyName("lastName")
        .build();

    PropertyExpression expr = sort.getEffectiveExpression();

    assertThat(expr).isInstanceOf(PropertyReference.class);
    assertThat(((PropertyReference) expr).getPropertyName()).isEqualTo("lastName");
  }

  @Test
  void givenExpression_whenGetEffectiveExpression_thenReturnExpression() {
    FunctionCall func = FunctionCall.of(Function.LOWER, PropertyReference.of("name"));
    SimpleSort sort = SimpleSort.builder()
        .expression(func)
        .build();

    PropertyExpression expr = sort.getEffectiveExpression();

    assertThat(expr).isEqualTo(func);
  }

  @Test
  void givenSimpleSort_whenToBuilder_thenReturnCorrectBuilder() {
    SimpleSort original = SimpleSort.builder()
        .propertyName("name")
        .direction(DESC)
        .build();

    SimpleSort copy = original.toBuilder().build();

    assertThat(copy).isEqualTo(original);
  }

  @Test
  void givenTwoEqualSimpleSorts_whenEquals_thenReturnTrue() {
    SimpleSort sort1 = SimpleSort.builder().propertyName("name").direction(ASC).build();
    SimpleSort sort2 = SimpleSort.builder().propertyName("name").direction(ASC).build();

    assertThat(sort1)
        .isEqualTo(sort2)
        .hasSameHashCodeAs(sort2);
  }

  @Test
  void givenDifferentSimpleSorts_whenEquals_thenReturnFalse() {
    SimpleSort sort1 = SimpleSort.builder().propertyName("name").direction(ASC).build();
    SimpleSort sort2 = SimpleSort.builder().propertyName("name").direction(DESC).build();

    assertThat(sort1).isNotEqualTo(sort2);
  }

  @Test
  void givenSimpleSort_whenToString_thenReturnReadableOutput() {
    SimpleSort sort = SimpleSort.builder()
        .propertyName("lastName")
        .direction(DESC)
        .build();

    String str = sort.toString();

    assertThat(str)
        .contains("lastName")
        .contains("DESC");
  }
}
