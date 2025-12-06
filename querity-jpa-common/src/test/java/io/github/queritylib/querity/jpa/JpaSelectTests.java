package io.github.queritylib.querity.jpa;

import io.github.queritylib.querity.api.NativeSelectWrapper;
import io.github.queritylib.querity.api.Select;
import io.github.queritylib.querity.api.SimpleSelect;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;
import jakarta.persistence.metamodel.Metamodel;
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

    @Test
    void givenNativeSelectWrapper_whenJpaNativeSelectWrapperOf_thenReturnJpaSelect() {
      NativeSelectWrapper<TestNativeSelection> wrapper = NativeSelectWrapper.<TestNativeSelection>builder()
          .nativeSelection(new TestNativeSelection("field1"))
          .build();

      JpaSelect jpaSelect = JpaNativeSelectWrapper.of(wrapper);

      assertThat(jpaSelect).isInstanceOf(TestJpaNativeSelectWrapper.class);
    }

    @Test
    void givenEmptyNativeSelectWrapper_whenJpaNativeSelectWrapperOf_thenThrowException() {
      NativeSelectWrapper<String> emptyWrapper = NativeSelectWrapper.<String>builder().build();

      assertThatThrownBy(() -> JpaNativeSelectWrapper.of(emptyWrapper))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("No JpaSelect implementation found");
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

  // Test native selection class
  public static class TestNativeSelection {
    private final String fieldName;

    public TestNativeSelection(String fieldName) {
      this.fieldName = fieldName;
    }

    public String getFieldName() {
      return fieldName;
    }
  }

  // Test JpaNativeSelectWrapper implementation
  public static class TestJpaNativeSelectWrapper extends JpaNativeSelectWrapper<TestNativeSelection> {

    public TestJpaNativeSelectWrapper(NativeSelectWrapper<TestNativeSelection> nativeSelectWrapper) {
      super(nativeSelectWrapper);
    }

    @Override
    public List<Selection<?>> toSelections(Metamodel metamodel, Root<?> root, CriteriaQuery<?> cq) {
      return List.of();
    }

    @Override
    public List<String> getPropertyNames() {
      return nativeSelectWrapper.getNativeSelections().stream()
          .map(TestNativeSelection::getFieldName)
          .toList();
    }
  }
}
