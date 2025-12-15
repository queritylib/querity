package io.github.queritylib.querity.jpa;

import io.github.queritylib.querity.api.NativeSortWrapper;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.Metamodel;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.github.queritylib.querity.api.Querity.sortByNative;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class JpaOrderSpecificationTests {

  @Nested
  class JpaOrderSpecificationSortWrapperTests {
    @SuppressWarnings("unchecked")
    @Test
    void givenOrderSpecification_whenOf_thenReturnJpaOrderSpecificationSortWrapper() {
      Order mockOrder = mock(Order.class);
      OrderSpecification<Object> orderSpec = (root, cb) -> mockOrder;
      NativeSortWrapper<OrderSpecification<?>> wrapper = sortByNative(orderSpec);

      JpaSort jpaSort = JpaSort.of(wrapper);

      assertThat(jpaSort).isInstanceOf(JpaOrderSpecificationSortWrapper.class);
    }

    @SuppressWarnings("unchecked")
    @Test
    void givenOrderSpecificationSortWrapper_whenToOrder_thenReturnOrderFromSpec() {
      Order mockOrder = mock(Order.class);
      OrderSpecification<Object> orderSpec = (root, cb) -> mockOrder;
      NativeSortWrapper<OrderSpecification<?>> wrapper = sortByNative(orderSpec);
      JpaOrderSpecificationSortWrapper sortWrapper = new JpaOrderSpecificationSortWrapper(wrapper);

      Metamodel metamodel = mock(Metamodel.class);
      Root<Object> root = mock(Root.class);
      CriteriaBuilder cb = mock(CriteriaBuilder.class);

      Order result = sortWrapper.toOrder(metamodel, root, cb);

      assertThat(result).isSameAs(mockOrder);
    }

    @Test
    void givenOrderSpecificationSortWrapper_whenGetNativeSort_thenReturnOrderSpecification() {
      Order mockOrder = mock(Order.class);
      OrderSpecification<Object> orderSpec = (root, cb) -> mockOrder;
      NativeSortWrapper<OrderSpecification<?>> wrapper = sortByNative(orderSpec);
      JpaOrderSpecificationSortWrapper sortWrapper = new JpaOrderSpecificationSortWrapper(wrapper);

      assertThat(sortWrapper.getNativeSort()).isSameAs(orderSpec);
    }
  }
}
