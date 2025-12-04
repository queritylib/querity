package io.github.queritylib.querity.spring.data.mongodb;

import io.github.queritylib.querity.api.NativeSortWrapper;
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

class MongodbSortTests {

  @Nested
  class FactoryMethodTests {
    @Test
    void givenSimpleSort_whenOf_thenReturnMongodbSimpleSort() {
      SimpleSort simpleSort = sortBy("lastName");

      MongodbSort mongodbSort = MongodbSort.of(simpleSort);

      assertThat(mongodbSort).isInstanceOf(MongodbSimpleSort.class);
    }

    @Test
    void givenNativeSortWrapper_whenOf_thenReturnMongodbNativeSortWrapper() {
      Order nativeOrder = Order.asc("lastName");
      NativeSortWrapper<Order> nativeSortWrapper = sortByNative(nativeOrder);

      MongodbSort mongodbSort = MongodbSort.of(nativeSortWrapper);

      assertThat(mongodbSort).isInstanceOf(MongodbNativeSortWrapper.class);
    }

    @Test
    void givenUnsupportedSort_whenOf_thenThrowIllegalArgumentException() {
      Sort unsupportedSort = new UnsupportedSort();

      assertThatThrownBy(() -> MongodbSort.of(unsupportedSort))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("UnsupportedSort")
          .hasMessageContaining("is not supported by the MongoDB module");
    }
  }

  @Nested
  class MongodbNativeSortWrapperTests {
    @Test
    void givenNativeSortWrapperAsc_whenToMongoSortOrder_thenReturnNativeOrder() {
      Order nativeOrder = Order.asc("lastName");
      NativeSortWrapper<Order> nativeSortWrapper = sortByNative(nativeOrder);

      MongodbSort mongodbSort = MongodbSort.of(nativeSortWrapper);
      Order result = mongodbSort.toMongoSortOrder();

      assertThat(result).isSameAs(nativeOrder);
      assertThat(result.getProperty()).isEqualTo("lastName");
      assertThat(result.getDirection()).isEqualTo(Direction.ASC);
    }

    @Test
    void givenNativeSortWrapperDesc_whenToMongoSortOrder_thenReturnNativeOrder() {
      Order nativeOrder = Order.desc("firstName");
      NativeSortWrapper<Order> nativeSortWrapper = sortByNative(nativeOrder);

      MongodbSort mongodbSort = MongodbSort.of(nativeSortWrapper);
      Order result = mongodbSort.toMongoSortOrder();

      assertThat(result).isSameAs(nativeOrder);
      assertThat(result.getProperty()).isEqualTo("firstName");
      assertThat(result.getDirection()).isEqualTo(Direction.DESC);
    }

    @Test
    void givenNativeSortWrapper_whenGetNativeSort_thenReturnOriginalOrder() {
      Order nativeOrder = Order.asc("lastName");
      NativeSortWrapper<Order> nativeSortWrapper = sortByNative(nativeOrder);

      MongodbNativeSortWrapper mongodbNativeSortWrapper = new MongodbNativeSortWrapper(nativeSortWrapper);

      assertThat(mongodbNativeSortWrapper.getNativeSort()).isSameAs(nativeOrder);
    }

    @Test
    void givenNativeSortWrapperWithIgnoreCase_whenToMongoSortOrder_thenPreserveOptions() {
      Order nativeOrder = Order.asc("lastName").ignoreCase();
      NativeSortWrapper<Order> nativeSortWrapper = sortByNative(nativeOrder);

      MongodbSort mongodbSort = MongodbSort.of(nativeSortWrapper);
      Order result = mongodbSort.toMongoSortOrder();

      assertThat(result.isIgnoreCase()).isTrue();
    }

    @Test
    void givenNativeSortWrapperWithNullHandling_whenToMongoSortOrder_thenPreserveOptions() {
      Order nativeOrder = Order.asc("lastName").nullsFirst();
      NativeSortWrapper<Order> nativeSortWrapper = sortByNative(nativeOrder);

      MongodbSort mongodbSort = MongodbSort.of(nativeSortWrapper);
      Order result = mongodbSort.toMongoSortOrder();

      assertThat(result.getNullHandling()).isEqualTo(org.springframework.data.domain.Sort.NullHandling.NULLS_FIRST);
    }
  }

  @Nested
  class MongodbSimpleSortTests {
    @Test
    void givenSimpleSortAsc_whenToMongoSortOrder_thenReturnAscendingOrder() {
      SimpleSort simpleSort = sortBy("lastName");

      MongodbSort mongodbSort = MongodbSort.of(simpleSort);
      Order result = mongodbSort.toMongoSortOrder();

      assertThat(result.getProperty()).isEqualTo("lastName");
      assertThat(result.getDirection()).isEqualTo(Direction.ASC);
      assertThat(result.getNullHandling()).isEqualTo(org.springframework.data.domain.Sort.NullHandling.NULLS_FIRST);
    }

    @Test
    void givenSimpleSortDesc_whenToMongoSortOrder_thenReturnDescendingOrder() {
      SimpleSort simpleSort = sortBy("firstName", io.github.queritylib.querity.api.SimpleSort.Direction.DESC);

      MongodbSort mongodbSort = MongodbSort.of(simpleSort);
      Order result = mongodbSort.toMongoSortOrder();

      assertThat(result.getProperty()).isEqualTo("firstName");
      assertThat(result.getDirection()).isEqualTo(Direction.DESC);
    }

    @Test
    void givenSimpleSort_whenGetPropertyName_thenReturnPropertyName() {
      SimpleSort simpleSort = sortBy("lastName");

      MongodbSimpleSort mongodbSimpleSort = new MongodbSimpleSort(simpleSort);

      assertThat(mongodbSimpleSort.getPropertyName()).isEqualTo("lastName");
    }

    @Test
    void givenSimpleSort_whenGetDirection_thenReturnDirection() {
      SimpleSort simpleSort = sortBy("lastName", io.github.queritylib.querity.api.SimpleSort.Direction.DESC);

      MongodbSimpleSort mongodbSimpleSort = new MongodbSimpleSort(simpleSort);

      assertThat(mongodbSimpleSort.getDirection()).isEqualTo(io.github.queritylib.querity.api.SimpleSort.Direction.DESC);
    }
  }

  // Helper class per testare il caso di Sort non supportato
  private static class UnsupportedSort implements Sort {
  }
}

