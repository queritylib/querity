package io.github.queritylib.querity.common.util;

import io.github.queritylib.querity.api.NativeSelectWrapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for SelectUtils focusing on type matching behavior with NativeSelectWrapper.
 * These tests verify that the SelectUtils can correctly match implementation classes
 * based on their constructor's generic parameter type.
 */
class SelectUtilsExtractRawTypeTests {

  @Test
  void givenImplementationWithGenericListParameter_whenGetSelectImplementation_thenMatchByListElementType() {
    // This tests the ParameterizedType branch in extractRawType()
    // The wrapper contains ArrayList (which implements List)
    NativeSelectWrapper<List<String>> wrapper = NativeSelectWrapper.<List<String>>builder()
        .nativeSelection(List.of("field1", "field2"))
        .build();
    Set<Class<? extends TestSelect>> implementationClasses = Set.of(ListBasedTestSelect.class);

    Optional<TestSelect> result = SelectUtils.getSelectImplementation(implementationClasses, wrapper);

    assertThat(result).isPresent();
    assertThat(result.get()).isInstanceOf(ListBasedTestSelect.class);
    assertThat(result.get().getPropertyNames()).containsExactly("field1", "field2");
  }

  @Test
  void givenMultipleImplementationsWithDifferentTypes_whenGetSelectImplementation_thenSelectCorrectOne() {
    NativeSelectWrapper<String> stringWrapper = NativeSelectWrapper.<String>builder()
        .nativeSelection("testField")
        .build();
    NativeSelectWrapper<Integer> intWrapper = NativeSelectWrapper.<Integer>builder()
        .nativeSelection(42)
        .build();

    Set<Class<? extends TestSelect>> implementationClasses = Set.of(
        StringBasedTestSelect.class,
        IntegerBasedTestSelect.class
    );

    Optional<TestSelect> stringResult = SelectUtils.getSelectImplementation(implementationClasses, stringWrapper);
    Optional<TestSelect> intResult = SelectUtils.getSelectImplementation(implementationClasses, intWrapper);

    assertThat(stringResult).isPresent();
    assertThat(stringResult.get()).isInstanceOf(StringBasedTestSelect.class);
    assertThat(stringResult.get().getPropertyNames()).containsExactly("testField");

    assertThat(intResult).isPresent();
    assertThat(intResult.get()).isInstanceOf(IntegerBasedTestSelect.class);
    assertThat(intResult.get().getPropertyNames()).containsExactly("42");
  }

  @Test
  void givenSubtypeOfExpectedType_whenGetSelectImplementation_thenMatchByInheritance() {
    // StringBuilder extends CharSequence, so it should match a CharSequence-based implementation
    NativeSelectWrapper<StringBuilder> wrapper = NativeSelectWrapper.<StringBuilder>builder()
        .nativeSelection(new StringBuilder("dynamicField"))
        .build();
    Set<Class<? extends TestSelect>> implementationClasses = Set.of(CharSequenceBasedTestSelect.class);

    Optional<TestSelect> result = SelectUtils.getSelectImplementation(implementationClasses, wrapper);

    assertThat(result).isPresent();
    assertThat(result.get()).isInstanceOf(CharSequenceBasedTestSelect.class);
    assertThat(result.get().getPropertyNames()).containsExactly("dynamicField");
  }

  // Test interface
  interface TestSelect {
    List<String> getPropertyNames();
  }

  // Implementation accepting List<String> - tests ParameterizedType handling
  public static class ListBasedTestSelect implements TestSelect {
    private final NativeSelectWrapper<List<String>> wrapper;

    public ListBasedTestSelect(NativeSelectWrapper<List<String>> wrapper) {
      this.wrapper = wrapper;
    }

    @Override
    public List<String> getPropertyNames() {
      return wrapper.getNativeSelections().stream()
          .flatMap(List::stream)
          .toList();
    }
  }

  // Implementation accepting String
  public static class StringBasedTestSelect implements TestSelect {
    private final NativeSelectWrapper<String> wrapper;

    public StringBasedTestSelect(NativeSelectWrapper<String> wrapper) {
      this.wrapper = wrapper;
    }

    @Override
    public List<String> getPropertyNames() {
      return wrapper.getNativeSelections();
    }
  }

  // Implementation accepting Integer
  public static class IntegerBasedTestSelect implements TestSelect {
    private final NativeSelectWrapper<Integer> wrapper;

    public IntegerBasedTestSelect(NativeSelectWrapper<Integer> wrapper) {
      this.wrapper = wrapper;
    }

    @Override
    public List<String> getPropertyNames() {
      return wrapper.getNativeSelections().stream()
          .map(Object::toString)
          .toList();
    }
  }

  // Implementation accepting CharSequence - tests inheritance matching
  public static class CharSequenceBasedTestSelect implements TestSelect {
    private final NativeSelectWrapper<? extends CharSequence> wrapper;

    public CharSequenceBasedTestSelect(NativeSelectWrapper<? extends CharSequence> wrapper) {
      this.wrapper = wrapper;
    }

    @Override
    public List<String> getPropertyNames() {
      return wrapper.getNativeSelections().stream()
          .map(CharSequence::toString)
          .toList();
    }
  }
}
