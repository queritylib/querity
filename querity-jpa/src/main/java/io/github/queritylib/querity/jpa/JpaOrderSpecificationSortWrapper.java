package io.github.queritylib.querity.jpa;

import io.github.queritylib.querity.api.NativeSortWrapper;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.Metamodel;
import lombok.experimental.Delegate;

/**
 * JPA-specific native sort wrapper that supports {@link OrderSpecification}.
 * This allows users to create native JPA sorts using a lambda that receives
 * the Root and CriteriaBuilder at query execution time.
 */
class JpaOrderSpecificationSortWrapper extends JpaSort {
  @Delegate
  private final NativeSortWrapper<OrderSpecification<?>> nativeSortWrapper;

  JpaOrderSpecificationSortWrapper(NativeSortWrapper<OrderSpecification<?>> nativeSortWrapper) {
    this.nativeSortWrapper = nativeSortWrapper;
  }

  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public <T> Order toOrder(Metamodel metamodel, Root<T> root, CriteriaBuilder cb) {
    return getNativeSort().toOrder((Root) root, cb);
  }
}

