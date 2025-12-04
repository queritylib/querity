package io.github.queritylib.querity.api;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.github.queritylib.querity.api.Querity.sortByNative;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NativeSortWrapperTest {

  @Nested
  class CreationTests {
    @Test
    void givenNativeSort_whenSortByNative_thenReturnNativeSortWrapper() {
      String nativeSort = "name ASC";
      NativeSortWrapper<String> wrapper = sortByNative(nativeSort);

      assertThat(wrapper).isNotNull();
      assertThat(wrapper.getNativeSort()).isEqualTo(nativeSort);
    }

    @Test
    void givenComplexNativeSort_whenSortByNative_thenReturnNativeSortWrapper() {
      // Simula un oggetto di sort nativo complesso
      TestNativeSort nativeSort = new TestNativeSort("lastName", "DESC");
      NativeSortWrapper<TestNativeSort> wrapper = sortByNative(nativeSort);

      assertThat(wrapper).isNotNull();
      assertThat(wrapper.getNativeSort()).isEqualTo(nativeSort);
      assertThat(wrapper.getNativeSort().getField()).isEqualTo("lastName");
      assertThat(wrapper.getNativeSort().getDirection()).isEqualTo("DESC");
    }

    @Test
    void givenNullNativeSort_whenSortByNative_thenThrowException() {
      assertThatThrownBy(() -> sortByNative(null))
          .isInstanceOf(NullPointerException.class);
    }
  }

  @Nested
  class EqualsAndHashCodeTests {
    @Test
    void givenTwoWrappersWithSameNativeSort_whenEquals_thenReturnTrue() {
      String nativeSort = "name ASC";
      NativeSortWrapper<String> wrapper1 = sortByNative(nativeSort);
      NativeSortWrapper<String> wrapper2 = sortByNative(nativeSort);

      assertThat(wrapper1).isEqualTo(wrapper2);
      assertThat(wrapper1.hashCode()).isEqualTo(wrapper2.hashCode());
    }

    @Test
    void givenTwoWrappersWithDifferentNativeSort_whenEquals_thenReturnFalse() {
      NativeSortWrapper<String> wrapper1 = sortByNative("name ASC");
      NativeSortWrapper<String> wrapper2 = sortByNative("name DESC");

      assertThat(wrapper1).isNotEqualTo(wrapper2);
    }
  }

  @Nested
  class SortInterfaceTests {
    @Test
    void givenNativeSortWrapper_whenCheckInterface_thenImplementsSort() {
      NativeSortWrapper<String> wrapper = sortByNative("name ASC");

      assertThat(wrapper).isInstanceOf(Sort.class);
    }
  }

  @Nested
  class BuilderTests {
    @Test
    void givenNativeSortWrapper_whenToBuilder_thenCanModify() {
      String originalSort = "name ASC";
      String newSort = "name DESC";
      NativeSortWrapper<String> original = sortByNative(originalSort);
      NativeSortWrapper<String> modified = original.toBuilder().nativeSort(newSort).build();

      assertThat(original.getNativeSort()).isEqualTo(originalSort);
      assertThat(modified.getNativeSort()).isEqualTo(newSort);
    }
  }

  @Nested
  class UsageInQueryTests {
    @Test
    void givenNativeSortWrapper_whenUsedInQuery_thenQueryHasSort() {
      NativeSortWrapper<String> wrapper = sortByNative("lastName ASC, firstName DESC");
      Query query = Querity.query()
          .sort(wrapper)
          .build();

      assertThat(query.hasSort()).isTrue();
      assertThat(query.getSort()).contains(wrapper);
    }

    @Test
    void givenMultipleNativeSortWrappers_whenUsedInQuery_thenQueryHasAllSorts() {
      NativeSortWrapper<String> wrapper1 = sortByNative("lastName ASC");
      NativeSortWrapper<String> wrapper2 = sortByNative("firstName DESC");
      Query query = Querity.query()
          .sort(wrapper1, wrapper2)
          .build();

      assertThat(query.hasSort()).isTrue();
      assertThat(query.getSort()).hasSize(2);
      assertThat(query.getSort()).contains(wrapper1, wrapper2);
    }

    @Test
    void givenMixedSortTypes_whenUsedInQuery_thenQueryHasAllSorts() {
      NativeSortWrapper<String> nativeSort = sortByNative("lastName ASC");
      SimpleSort simpleSort = Querity.sortBy("firstName", SimpleSort.Direction.DESC);
      Query query = Querity.query()
          .sort(nativeSort, simpleSort)
          .build();

      assertThat(query.hasSort()).isTrue();
      assertThat(query.getSort()).hasSize(2);
      assertThat(query.getSort()).contains(nativeSort, simpleSort);
    }
  }

  // Helper class per simulare un oggetto di sort nativo complesso
  private static class TestNativeSort {
    private final String field;
    private final String direction;

    public TestNativeSort(String field, String direction) {
      this.field = field;
      this.direction = direction;
    }

    public String getField() {
      return field;
    }

    public String getDirection() {
      return direction;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      TestNativeSort that = (TestNativeSort) o;
      return field.equals(that.field) && direction.equals(that.direction);
    }

    @Override
    public int hashCode() {
      return 31 * field.hashCode() + direction.hashCode();
    }
  }
}

