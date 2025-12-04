package io.github.queritylib.querity.jpa;

import io.github.queritylib.querity.api.NativeSortWrapper;
import io.github.queritylib.querity.api.SimpleSort;
import io.github.queritylib.querity.api.Sort;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.Metamodel;

import java.util.Optional;
import java.util.Set;

import static io.github.queritylib.querity.common.util.ReflectionUtils.findSubclasses;
import static io.github.queritylib.querity.common.util.SortUtils.getSortImplementation;

abstract class JpaSort {

  private static final Set<Class<? extends JpaSort>> JPA_SORT_IMPLEMENTATIONS = findSubclasses(JpaSort.class);

  public abstract <T> Order toOrder(Metamodel metamodel, Root<T> root, CriteriaBuilder cb);

  @SuppressWarnings("unchecked")
  public static JpaSort of(Sort sort) {
    if (sort instanceof SimpleSort simpleSort) {
      return new JpaSimpleSort(simpleSort);
    } else if (sort instanceof NativeSortWrapper<?> nativeSortWrapper) {
      Optional<JpaSort> implementation = getSortImplementation(JPA_SORT_IMPLEMENTATIONS, nativeSortWrapper);
      if (implementation.isPresent()) {
        return implementation.get();
      }
      // Fallback to JpaNativeSortWrapper for Order types
      if (nativeSortWrapper.getNativeSort() instanceof Order) {
        return new JpaNativeSortWrapper((NativeSortWrapper<Order>) sort);
      }
      throw new IllegalArgumentException(
          String.format("NativeSortWrapper with type %s is not supported by the JPA module. " +
              "Use Order for direct JPA ordering or OrderSpecification for deferred ordering.",
              nativeSortWrapper.getNativeSort().getClass().getSimpleName()));
    }
    throw new IllegalArgumentException(
        String.format("Sort class %s is not supported by the JPA module", sort.getClass().getSimpleName()));
  }
}

