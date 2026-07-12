package io.github.queritylib.querity.common.util;

import io.github.queritylib.querity.api.NativeSelectWrapper;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class SelectUtilsTests {

  @Test
  void givenEmptyNativeSelections_whenGetSelectImplementation_thenReturnEmpty() {
    NativeSelectWrapper<String> emptyWrapper = NativeSelectWrapper.<String>builder().build();
    Set<Class<? extends TestSelect>> implementationClasses = Set.of(StringTestSelect.class);

    Optional<TestSelect> result = SelectUtils.getSelectImplementation(implementationClasses, emptyWrapper);

    assertThat(result).isEmpty();
  }

  @Test
  void givenMatchingNativeSelectWrapper_whenGetSelectImplementation_thenReturnInstance() {
    NativeSelectWrapper<String> wrapper = NativeSelectWrapper.<String>builder()
        .nativeSelection("field1")
        .nativeSelection("field2")
        .build();
    Set<Class<? extends TestSelect>> implementationClasses = Set.of(StringTestSelect.class);

    Optional<TestSelect> result = SelectUtils.getSelectImplementation(implementationClasses, wrapper);

    assertThat(result).isPresent();
    assertThat(result.get()).isInstanceOf(StringTestSelect.class);
  }

  @Test
  void givenNoMatchingConstructor_whenGetSelectImplementation_thenReturnEmpty() {
    NativeSelectWrapper<Integer> wrapper = NativeSelectWrapper.<Integer>builder()
        .nativeSelection(1)
        .build();
    Set<Class<? extends TestSelect>> implementationClasses = Set.of(StringTestSelect.class);

    Optional<TestSelect> result = SelectUtils.getSelectImplementation(implementationClasses, wrapper);

    assertThat(result).isEmpty();
  }

  @Test
  void givenMultipleImplementations_whenGetSelectImplementation_thenReturnMatchingOne() {
    NativeSelectWrapper<Integer> wrapper = NativeSelectWrapper.<Integer>builder()
        .nativeSelection(42)
        .build();
    Set<Class<? extends TestSelect>> implementationClasses = new HashSet<>();
    implementationClasses.add(StringTestSelect.class);
    implementationClasses.add(IntegerTestSelect.class);

    Optional<TestSelect> result = SelectUtils.getSelectImplementation(implementationClasses, wrapper);

    assertThat(result).isPresent();
    assertThat(result.get()).isInstanceOf(IntegerTestSelect.class);
  }

  @Test
  void givenImplementationWithNoArgConstructorOnly_whenGetSelectImplementation_thenReturnEmpty() {
    NativeSelectWrapper<String> wrapper = NativeSelectWrapper.<String>builder()
        .nativeSelection("field1")
        .build();
    Set<Class<? extends TestSelect>> implementationClasses = Set.of(NoArgConstructorTestSelect.class);

    Optional<TestSelect> result = SelectUtils.getSelectImplementation(implementationClasses, wrapper);

    assertThat(result).isEmpty();
  }

  @Test
  void givenImplementationWithNonWrapperConstructor_whenGetSelectImplementation_thenReturnEmpty() {
    NativeSelectWrapper<String> wrapper = NativeSelectWrapper.<String>builder()
        .nativeSelection("field1")
        .build();
    Set<Class<? extends TestSelect>> implementationClasses = Set.of(NonWrapperConstructorTestSelect.class);

    Optional<TestSelect> result = SelectUtils.getSelectImplementation(implementationClasses, wrapper);

    assertThat(result).isEmpty();
  }

  @Test
  void givenImplementationWithParameterizedNonWrapperConstructor_whenGetSelectImplementation_thenReturnEmpty() {
    NativeSelectWrapper<String> wrapper = NativeSelectWrapper.<String>builder()
        .nativeSelection("field1")
        .build();
    Set<Class<? extends TestSelect>> implementationClasses = Set.of(ParameterizedNonWrapperConstructorTestSelect.class);

    Optional<TestSelect> result = SelectUtils.getSelectImplementation(implementationClasses, wrapper);

    assertThat(result).isEmpty();
  }

  @Test
  void givenImplementationWithTypeVariableInWrapper_whenGetSelectImplementation_thenReturnEmpty() {
    NativeSelectWrapper<String> wrapper = NativeSelectWrapper.<String>builder()
        .nativeSelection("field1")
        .build();
    Set<Class<? extends TestSelect>> implementationClasses = Set.of(GenericTestSelect.class);

    Optional<TestSelect> result = SelectUtils.getSelectImplementation(implementationClasses, wrapper);

    assertThat(result).isEmpty();
  }

  // Test interfaces and implementations
  interface TestSelect {
    List<String> getPropertyNames();
  }

  public static class StringTestSelect implements TestSelect {
    private final NativeSelectWrapper<String> wrapper;

    public StringTestSelect(NativeSelectWrapper<String> wrapper) {
      this.wrapper = wrapper;
    }

    @Override
    public List<String> getPropertyNames() {
      return wrapper.getNativeSelections();
    }
  }

  public static class IntegerTestSelect implements TestSelect {
    private final NativeSelectWrapper<Integer> wrapper;

    public IntegerTestSelect(NativeSelectWrapper<Integer> wrapper) {
      this.wrapper = wrapper;
    }

    @Override
    public List<String> getPropertyNames() {
      return wrapper.getNativeSelections().stream().map(Object::toString).toList();
    }
  }

  // Test implementation with only a no-arg constructor
  public static class NoArgConstructorTestSelect implements TestSelect {
    public NoArgConstructorTestSelect() {
    }

    @Override
    public List<String> getPropertyNames() {
      return List.of();
    }
  }

  // Test implementation with a non-NativeSelectWrapper constructor
  public static class NonWrapperConstructorTestSelect implements TestSelect {
    public NonWrapperConstructorTestSelect(String value) {
    }

    @Override
    public List<String> getPropertyNames() {
      return List.of();
    }
  }

  // Test implementation with a parameterized constructor parameter that is not a NativeSelectWrapper
  public static class ParameterizedNonWrapperConstructorTestSelect implements TestSelect {
    public ParameterizedNonWrapperConstructorTestSelect(List<String> values) {
    }

    @Override
    public List<String> getPropertyNames() {
      return List.of();
    }
  }

  // Test implementation with a type variable in the wrapper (raw type cannot be extracted)
  public static class GenericTestSelect<T> implements TestSelect {
    public GenericTestSelect(NativeSelectWrapper<T> wrapper) {
    }

    @Override
    public List<String> getPropertyNames() {
      return List.of();
    }
  }
}
