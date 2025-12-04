package io.github.queritylib.querity.jpa;

import io.github.queritylib.querity.api.NativeSortWrapper;
import io.github.queritylib.querity.api.SimpleSort;
import io.github.queritylib.querity.api.Sort;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.Metamodel;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.github.queritylib.querity.api.Querity.sortBy;
import static io.github.queritylib.querity.api.Querity.sortByNative;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class JpaSortTests {

  @Nested
  class FactoryMethodTests {
    @Test
    void givenSimpleSort_whenOf_thenReturnJpaSimpleSort() {
      SimpleSort simpleSort = sortBy("lastName");

      JpaSort jpaSort = JpaSort.of(simpleSort);

      assertThat(jpaSort).isInstanceOf(JpaSimpleSort.class);
    }

    @Test
    void givenNativeSortWrapper_whenOf_thenReturnJpaNativeSortWrapper() {
      Order mockOrder = mock(Order.class);
      NativeSortWrapper<Order> nativeSortWrapper = sortByNative(mockOrder);

      JpaSort jpaSort = JpaSort.of(nativeSortWrapper);

      assertThat(jpaSort).isInstanceOf(JpaNativeSortWrapper.class);
    }

    @Test
    void givenUnsupportedSort_whenOf_thenThrowIllegalArgumentException() {
      Sort unsupportedSort = new UnsupportedSort();

      assertThatThrownBy(() -> JpaSort.of(unsupportedSort))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("UnsupportedSort")
          .hasMessageContaining("is not supported by the JPA module");
    }

    @Test
    void givenNativeSortWrapperWithUnsupportedType_whenOf_thenThrowIllegalArgumentException() {
      // Using a String instead of Order or OrderSpecification
      NativeSortWrapper<String> nativeSortWrapper = sortByNative("unsupportedType");

      assertThatThrownBy(() -> JpaSort.of(nativeSortWrapper))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("String")
          .hasMessageContaining("is not supported by the JPA module");
    }
  }

  @Nested
  class JpaNativeSortWrapperTests {
    @Test
    void givenNativeSortWrapper_whenToOrder_thenReturnNativeOrder() {
      Order mockOrder = mock(Order.class);
      NativeSortWrapper<Order> nativeSortWrapper = sortByNative(mockOrder);

      JpaSort jpaSort = JpaSort.of(nativeSortWrapper);
      Metamodel mockMetamodel = mock(Metamodel.class);
      Root<?> mockRoot = mock(Root.class);
      CriteriaBuilder mockCb = mock(CriteriaBuilder.class);

      Order result = jpaSort.toOrder(mockMetamodel, mockRoot, mockCb);

      assertThat(result).isSameAs(mockOrder);
    }

    @Test
    void givenNativeSortWrapper_whenGetNativeSort_thenReturnOriginalOrder() {
      Order mockOrder = mock(Order.class);
      NativeSortWrapper<Order> nativeSortWrapper = sortByNative(mockOrder);

      JpaNativeSortWrapper jpaNativeSortWrapper = new JpaNativeSortWrapper(nativeSortWrapper);

      assertThat(jpaNativeSortWrapper.getNativeSort()).isSameAs(mockOrder);
    }
  }

  @Nested
  class JpaSimpleSortTests {
    @Test
    void givenSimpleSort_whenGetPropertyName_thenReturnPropertyName() {
      SimpleSort simpleSort = sortBy("lastName");
      JpaSimpleSort jpaSimpleSort = new JpaSimpleSort(simpleSort);

      assertThat(jpaSimpleSort.getPropertyName()).isEqualTo("lastName");
    }

    @Test
    void givenSimpleSort_whenGetDirection_thenReturnDirection() {
      SimpleSort simpleSort = sortBy("lastName", SimpleSort.Direction.DESC);
      JpaSimpleSort jpaSimpleSort = new JpaSimpleSort(simpleSort);

      assertThat(jpaSimpleSort.getDirection()).isEqualTo(SimpleSort.Direction.DESC);
    }

    @Test
    void givenSimpleSortAsc_whenGetDirection_thenReturnAsc() {
      SimpleSort simpleSort = sortBy("firstName");
      JpaSimpleSort jpaSimpleSort = new JpaSimpleSort(simpleSort);

      assertThat(jpaSimpleSort.getDirection()).isEqualTo(SimpleSort.Direction.ASC);
    }
  }

  // Helper class per testare il caso di Sort non supportato
  private static class UnsupportedSort implements Sort {
  }
}

