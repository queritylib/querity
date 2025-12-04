package io.github.queritylib.querity.jpa;

import io.github.queritylib.querity.api.NativeSortWrapper;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.Metamodel;
import lombok.experimental.Delegate;

class JpaNativeSortWrapper extends JpaSort {
  @Delegate
  private final NativeSortWrapper<Order> nativeSortWrapper;

  JpaNativeSortWrapper(NativeSortWrapper<Order> nativeSortWrapper) {
    this.nativeSortWrapper = nativeSortWrapper;
  }

  @Override
  public <T> Order toOrder(Metamodel metamodel, Root<T> root, CriteriaBuilder cb) {
    return getNativeSort();
  }
}

