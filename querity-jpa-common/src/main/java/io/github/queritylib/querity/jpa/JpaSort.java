package io.github.queritylib.querity.jpa;

import io.github.queritylib.querity.api.NativeSortWrapper;
import io.github.queritylib.querity.api.SimpleSort;
import io.github.queritylib.querity.api.Sort;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.Metamodel;

abstract class JpaSort {

  public abstract <T> Order toOrder(Metamodel metamodel, Root<T> root, CriteriaBuilder cb);

  @SuppressWarnings("unchecked")
  public static JpaSort of(Sort sort) {
    if (sort instanceof SimpleSort simpleSort) {
      return new JpaSimpleSort(simpleSort);
    } else if (sort instanceof NativeSortWrapper) {
      return new JpaNativeSortWrapper((NativeSortWrapper<Order>) sort);
    }
    throw new IllegalArgumentException(
        String.format("Sort class %s is not supported by the JPA module", sort.getClass().getSimpleName()));
  }
}

