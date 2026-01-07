package io.github.queritylib.querity.api;

import org.junit.jupiter.api.Test;


import static io.github.queritylib.querity.api.Querity.selectByNative;
import static org.assertj.core.api.Assertions.assertThat;

class NativeSelectWrapperTest {

  @Test
  void givenNativeSelections_whenSelectByNative_thenReturnNativeSelectWrapper() {
    NativeSelectWrapper<String> wrapper = selectByNative("field1", "field2");

    assertThat(wrapper).isNotNull();
    assertThat(wrapper.getNativeSelections()).containsExactly("field1", "field2");
  }

  @Test
  void givenNativeSelectWrapper_whenGetPropertyNames_thenReturnEmptyList() {
    NativeSelectWrapper<String> wrapper = selectByNative("field1");

    assertThat(wrapper.getPropertyNames()).isEmpty();
  }

  @Test
  void givenNativeSelectWrapper_whenImplementsSelect_thenTrue() {
    NativeSelectWrapper<String> wrapper = selectByNative("field1");

    assertThat(wrapper).isInstanceOf(Select.class);
  }

  @Test
  void givenTwoEqualNativeSelectWrappers_whenEquals_thenReturnTrue() {
    NativeSelectWrapper<String> wrapper1 = selectByNative("field1", "field2");
    NativeSelectWrapper<String> wrapper2 = selectByNative("field1", "field2");

    assertThat(wrapper1)
        .isEqualTo(wrapper2)
        .hasSameHashCodeAs(wrapper2);
  }

  @Test
  void givenNativeSelectWrapper_whenToString_thenContainsSelections() {
    NativeSelectWrapper<String> wrapper = selectByNative("field1", "field2");

    assertThat(wrapper.toString()).contains("field1", "field2");
  }

  @Test
  void givenNativeSelectWrapper_whenToBuilder_thenCanModify() {
    NativeSelectWrapper<String> original = selectByNative("field1");
    NativeSelectWrapper<String> modified = original.toBuilder()
        .nativeSelection("field2")
        .build();

    assertThat(original.getNativeSelections()).containsExactly("field1");
    assertThat(modified.getNativeSelections()).containsExactly("field1", "field2");
  }
}
