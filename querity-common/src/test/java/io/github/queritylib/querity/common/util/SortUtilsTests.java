package io.github.queritylib.querity.common.util;

import io.github.queritylib.querity.api.NativeSortWrapper;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static io.github.queritylib.querity.api.Querity.sortByNative;
import static org.assertj.core.api.Assertions.assertThat;

class SortUtilsTests {

  @Test
  void givenNativeSortWrapperWithMatchingImplementation_whenGetSortImplementation_thenReturnImplementation() {
    String nativeSort = "testSort";
    NativeSortWrapper<String> wrapper = sortByNative(nativeSort);
    Set<Class<? extends SortImplementation>> classes = new HashSet<>();
    classes.add(StringSortImplementation.class);
    classes.add(IntegerSortImplementation.class);

    Optional<SortImplementation> result = SortUtils.getSortImplementation(classes, wrapper);

    assertThat(result)
        .isPresent()
        .get()
        .isInstanceOf(StringSortImplementation.class);
  }

  @Test
  void givenNativeSortWrapperWithNoMatchingImplementation_whenGetSortImplementation_thenReturnEmpty() {
    Double nativeSort = 3.14;
    NativeSortWrapper<Double> wrapper = sortByNative(nativeSort);
    Set<Class<? extends SortImplementation>> classes = new HashSet<>();
    classes.add(StringSortImplementation.class);
    classes.add(IntegerSortImplementation.class);

    Optional<SortImplementation> result = SortUtils.getSortImplementation(classes, wrapper);

    assertThat(result).isEmpty();
  }

  @Test
  void givenNativeSortWrapperWithIntegerNativeSort_whenGetSortImplementation_thenReturnIntegerImplementation() {
    Integer nativeSort = 42;
    NativeSortWrapper<Integer> wrapper = sortByNative(nativeSort);
    Set<Class<? extends SortImplementation>> classes = new HashSet<>();
    classes.add(StringSortImplementation.class);
    classes.add(IntegerSortImplementation.class);

    Optional<SortImplementation> result = SortUtils.getSortImplementation(classes, wrapper);

    assertThat(result)
        .isPresent()
        .get()
        .isInstanceOf(IntegerSortImplementation.class);
  }

  @Test
  void givenEmptyImplementationClasses_whenGetSortImplementation_thenReturnEmpty() {
    String nativeSort = "testSort";
    NativeSortWrapper<String> wrapper = sortByNative(nativeSort);
    Set<Class<? extends SortImplementation>> classes = new HashSet<>();

    Optional<SortImplementation> result = SortUtils.getSortImplementation(classes, wrapper);

    assertThat(result).isEmpty();
  }

  @Test
  void givenNativeSortWrapperWithSubtype_whenGetSortImplementation_thenReturnMatchingImplementation() {
    // Test that assignability works (Number is assignable from Integer)
    Integer nativeSort = 100;
    NativeSortWrapper<Integer> wrapper = sortByNative(nativeSort);
    Set<Class<? extends SortImplementation>> classes = new HashSet<>();
    classes.add(NumberSortImplementation.class);

    Optional<SortImplementation> result = SortUtils.getSortImplementation(classes, wrapper);

    assertThat(result)
        .isPresent()
        .get()
        .isInstanceOf(NumberSortImplementation.class);
  }

  @Test
  void givenNativeSortWrapperWithWildcardType_whenGetSortImplementation_thenReturnMatchingImplementation() {
    String nativeSort = "wildcard";
    NativeSortWrapper<String> wrapper = sortByNative(nativeSort);
    Set<Class<? extends SortImplementation>> classes = new HashSet<>();
    classes.add(WildcardSortImplementation.class);

    Optional<SortImplementation> result = SortUtils.getSortImplementation(classes, wrapper);

    assertThat(result)
        .isPresent()
        .get()
        .isInstanceOf(WildcardSortImplementation.class);
  }

  @Test
  void givenImplementationWithMultipleConstructors_whenGetSortImplementation_thenReturnMatchingImplementation() {
    String nativeSort = "test";
    NativeSortWrapper<String> wrapper = sortByNative(nativeSort);
    Set<Class<? extends SortImplementation>> classes = new HashSet<>();
    classes.add(MultiConstructorSortImplementation.class);

    Optional<SortImplementation> result = SortUtils.getSortImplementation(classes, wrapper);

    assertThat(result)
        .isPresent()
        .get()
        .isInstanceOf(MultiConstructorSortImplementation.class);
  }

  @Test
  void givenImplementationWithNoArgConstructorOnly_whenGetSortImplementation_thenReturnEmpty() {
    String nativeSort = "test";
    NativeSortWrapper<String> wrapper = sortByNative(nativeSort);
    Set<Class<? extends SortImplementation>> classes = new HashSet<>();
    classes.add(NoArgConstructorSortImplementation.class);

    Optional<SortImplementation> result = SortUtils.getSortImplementation(classes, wrapper);

    assertThat(result).isEmpty();
  }

  @Test
  void givenImplementationWithNonNativeSortWrapperConstructor_whenGetSortImplementation_thenReturnEmpty() {
    String nativeSort = "test";
    NativeSortWrapper<String> wrapper = sortByNative(nativeSort);
    Set<Class<? extends SortImplementation>> classes = new HashSet<>();
    classes.add(NonWrapperConstructorSortImplementation.class);

    Optional<SortImplementation> result = SortUtils.getSortImplementation(classes, wrapper);

    assertThat(result).isEmpty();
  }

  @Test
  void givenImplementationWithParameterizedTypeInWrapper_whenGetSortImplementation_thenReturnMatchingImplementation() {
    java.util.List<String> nativeSort = java.util.Arrays.asList("a", "b");
    NativeSortWrapper<java.util.List<String>> wrapper = sortByNative(nativeSort);
    Set<Class<? extends SortImplementation>> classes = new HashSet<>();
    classes.add(ListSortImplementation.class);

    Optional<SortImplementation> result = SortUtils.getSortImplementation(classes, wrapper);

    assertThat(result)
        .isPresent()
        .get()
        .isInstanceOf(ListSortImplementation.class);
  }

  // Test interface
  interface SortImplementation {
  }

  // Test implementation that accepts NativeSortWrapper<String>
  public static class StringSortImplementation implements SortImplementation {
    private final NativeSortWrapper<String> wrapper;

    public StringSortImplementation(NativeSortWrapper<String> wrapper) {
      this.wrapper = wrapper;
    }
  }

  // Test implementation that accepts NativeSortWrapper<Integer>
  public static class IntegerSortImplementation implements SortImplementation {
    private final NativeSortWrapper<Integer> wrapper;

    public IntegerSortImplementation(NativeSortWrapper<Integer> wrapper) {
      this.wrapper = wrapper;
    }
  }

  // Test implementation that accepts NativeSortWrapper<Number> (to test assignability)
  public static class NumberSortImplementation implements SortImplementation {
    private final NativeSortWrapper<Number> wrapper;

    public NumberSortImplementation(NativeSortWrapper<Number> wrapper) {
      this.wrapper = wrapper;
    }
  }

  // Test implementation that accepts NativeSortWrapper<? extends CharSequence> (to test wildcard types)
  public static class WildcardSortImplementation implements SortImplementation {
    private final NativeSortWrapper<? extends CharSequence> wrapper;

    public WildcardSortImplementation(NativeSortWrapper<? extends CharSequence> wrapper) {
      this.wrapper = wrapper;
    }
  }

  // Test implementation with multiple constructors
  public static class MultiConstructorSortImplementation implements SortImplementation {
    private final NativeSortWrapper<String> wrapper;

    public MultiConstructorSortImplementation() {
      this.wrapper = null;
    }

    public MultiConstructorSortImplementation(NativeSortWrapper<String> wrapper) {
      this.wrapper = wrapper;
    }

    public MultiConstructorSortImplementation(String other, int value) {
      this.wrapper = null;
    }
  }

  // Test implementation with only no-arg constructor
  public static class NoArgConstructorSortImplementation implements SortImplementation {
    public NoArgConstructorSortImplementation() {
    }
  }

  // Test implementation with non-NativeSortWrapper constructor
  public static class NonWrapperConstructorSortImplementation implements SortImplementation {
    public NonWrapperConstructorSortImplementation(String value) {
    }
  }

  // Test implementation that accepts NativeSortWrapper<List> (to test ParameterizedType extraction)
  public static class ListSortImplementation implements SortImplementation {
    private final NativeSortWrapper<java.util.List<?>> wrapper;

    public ListSortImplementation(NativeSortWrapper<java.util.List<?>> wrapper) {
      this.wrapper = wrapper;
    }
  }
}

