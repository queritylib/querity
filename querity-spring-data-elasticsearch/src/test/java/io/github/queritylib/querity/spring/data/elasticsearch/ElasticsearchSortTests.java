package io.github.queritylib.querity.spring.data.elasticsearch;

import io.github.queritylib.querity.api.Function;
import io.github.queritylib.querity.api.FunctionCall;
import io.github.queritylib.querity.api.NativeSortWrapper;
import io.github.queritylib.querity.api.PropertyReference;
import io.github.queritylib.querity.api.SimpleSort;
import io.github.queritylib.querity.api.Sort;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

import static io.github.queritylib.querity.api.Querity.sortBy;
import static io.github.queritylib.querity.api.Querity.sortByNative;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ElasticsearchSortTests {

  @Nested
  class FactoryMethodTests {
    @Test
    void givenSimpleSort_whenOf_thenReturnElasticsearchSimpleSort() {
      SimpleSort simpleSort = sortBy("lastName");

      ElasticsearchSort elasticsearchSort = ElasticsearchSort.of(simpleSort);

      assertThat(elasticsearchSort).isInstanceOf(ElasticsearchSimpleSort.class);
    }

    @Test
    void givenNativeSortWrapper_whenOf_thenReturnElasticsearchNativeSortWrapper() {
      Order nativeOrder = Order.asc("lastName");
      NativeSortWrapper<Order> nativeSortWrapper = sortByNative(nativeOrder);

      ElasticsearchSort elasticsearchSort = ElasticsearchSort.of(nativeSortWrapper);

      assertThat(elasticsearchSort).isInstanceOf(ElasticsearchNativeSortWrapper.class);
    }

    @Test
    void givenUnsupportedSort_whenOf_thenThrowIllegalArgumentException() {
      Sort unsupportedSort = new UnsupportedSort();

      assertThatThrownBy(() -> ElasticsearchSort.of(unsupportedSort))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("UnsupportedSort")
          .hasMessageContaining("is not supported by the Elasticsearch module");
    }
  }

  @Nested
  class ElasticsearchNativeSortWrapperTests {
    @Test
    void givenNativeSortWrapperAsc_whenToElasticsearchSortOrder_thenReturnNativeOrder() {
      Order nativeOrder = Order.asc("lastName");
      NativeSortWrapper<Order> nativeSortWrapper = sortByNative(nativeOrder);

      ElasticsearchSort elasticsearchSort = ElasticsearchSort.of(nativeSortWrapper);
      Order result = elasticsearchSort.toElasticsearchSortOrder();

      assertThat(result).isSameAs(nativeOrder);
      assertThat(result.getProperty()).isEqualTo("lastName");
      assertThat(result.getDirection()).isEqualTo(Direction.ASC);
    }

    @Test
    void givenNativeSortWrapperDesc_whenToElasticsearchSortOrder_thenReturnNativeOrder() {
      Order nativeOrder = Order.desc("firstName");
      NativeSortWrapper<Order> nativeSortWrapper = sortByNative(nativeOrder);

      ElasticsearchSort elasticsearchSort = ElasticsearchSort.of(nativeSortWrapper);
      Order result = elasticsearchSort.toElasticsearchSortOrder();

      assertThat(result).isSameAs(nativeOrder);
      assertThat(result.getProperty()).isEqualTo("firstName");
      assertThat(result.getDirection()).isEqualTo(Direction.DESC);
    }

    @Test
    void givenNativeSortWrapper_whenGetNativeSort_thenReturnOriginalOrder() {
      Order nativeOrder = Order.asc("lastName");
      NativeSortWrapper<Order> nativeSortWrapper = sortByNative(nativeOrder);

      ElasticsearchNativeSortWrapper elasticsearchNativeSortWrapper = new ElasticsearchNativeSortWrapper(nativeSortWrapper);

      assertThat(elasticsearchNativeSortWrapper.getNativeSort()).isSameAs(nativeOrder);
    }

    @Test
    void givenNativeSortWrapperWithIgnoreCase_whenToElasticsearchSortOrder_thenPreserveOptions() {
      Order nativeOrder = Order.asc("lastName").ignoreCase();
      NativeSortWrapper<Order> nativeSortWrapper = sortByNative(nativeOrder);

      ElasticsearchSort elasticsearchSort = ElasticsearchSort.of(nativeSortWrapper);
      Order result = elasticsearchSort.toElasticsearchSortOrder();

      assertThat(result.isIgnoreCase()).isTrue();
    }

    @Test
    void givenNativeSortWrapperWithNullHandling_whenToElasticsearchSortOrder_thenPreserveOptions() {
      Order nativeOrder = Order.asc("lastName").nullsLast();
      NativeSortWrapper<Order> nativeSortWrapper = sortByNative(nativeOrder);

      ElasticsearchSort elasticsearchSort = ElasticsearchSort.of(nativeSortWrapper);
      Order result = elasticsearchSort.toElasticsearchSortOrder();

      assertThat(result.getNullHandling()).isEqualTo(org.springframework.data.domain.Sort.NullHandling.NULLS_LAST);
    }
  }

  @Nested
  class ElasticsearchSimpleSortTests {
    @Test
    void givenSimpleSortAsc_whenToElasticsearchSortOrder_thenReturnAscendingOrder() {
      SimpleSort simpleSort = sortBy("lastName");

      ElasticsearchSort elasticsearchSort = ElasticsearchSort.of(simpleSort);
      Order result = elasticsearchSort.toElasticsearchSortOrder();

      assertThat(result.getProperty()).isEqualTo("lastName");
      assertThat(result.getDirection()).isEqualTo(Direction.ASC);
      assertThat(result.getNullHandling()).isEqualTo(org.springframework.data.domain.Sort.NullHandling.NULLS_LAST);
    }

    @Test
    void givenSimpleSortDesc_whenToElasticsearchSortOrder_thenReturnDescendingOrder() {
      SimpleSort simpleSort = sortBy("firstName", io.github.queritylib.querity.api.SimpleSort.Direction.DESC);

      ElasticsearchSort elasticsearchSort = ElasticsearchSort.of(simpleSort);
      Order result = elasticsearchSort.toElasticsearchSortOrder();

      assertThat(result.getProperty()).isEqualTo("firstName");
      assertThat(result.getDirection()).isEqualTo(Direction.DESC);
    }

    @Test
    void givenSimpleSort_whenGetPropertyName_thenReturnPropertyName() {
      SimpleSort simpleSort = sortBy("lastName");

      ElasticsearchSimpleSort elasticsearchSimpleSort = new ElasticsearchSimpleSort(simpleSort);

      assertThat(elasticsearchSimpleSort.getPropertyName()).isEqualTo("lastName");
    }

    @Test
    void givenSimpleSort_whenGetDirection_thenReturnDirection() {
      SimpleSort simpleSort = sortBy("lastName", io.github.queritylib.querity.api.SimpleSort.Direction.DESC);

      ElasticsearchSimpleSort elasticsearchSimpleSort = new ElasticsearchSimpleSort(simpleSort);

      assertThat(elasticsearchSimpleSort.getDirection()).isEqualTo(io.github.queritylib.querity.api.SimpleSort.Direction.DESC);
    }

    @Test
    void givenSimpleSortWithPropertyExpression_whenToElasticsearchSortOrder_thenReturnOrder() {
      SimpleSort simpleSort = sortBy(PropertyReference.of("email"), SimpleSort.Direction.ASC);

      ElasticsearchSort elasticsearchSort = ElasticsearchSort.of(simpleSort);
      Order result = elasticsearchSort.toElasticsearchSortOrder();

      assertThat(result.getProperty()).isEqualTo("email");
      assertThat(result.getDirection()).isEqualTo(Direction.ASC);
    }

    @Test
    void givenSimpleSortWithFunctionExpression_whenToElasticsearchSortOrder_thenThrowUnsupportedOperationException() {
      FunctionCall lowerName = FunctionCall.of(Function.LOWER, PropertyReference.of("name"));
      SimpleSort simpleSort = sortBy(lowerName, SimpleSort.Direction.ASC);

      ElasticsearchSort elasticsearchSort = ElasticsearchSort.of(simpleSort);

      assertThatThrownBy(() -> elasticsearchSort.toElasticsearchSortOrder())
          .isInstanceOf(UnsupportedOperationException.class)
          .hasMessageContaining("Function LOWER is not supported in Elasticsearch");
    }
  }

  // Helper class to test unsupported Sort case
  private static class UnsupportedSort implements Sort {
  }
}

