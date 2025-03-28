package io.github.queritylib.querity.spring.data.jpa;

import io.github.queritylib.querity.api.Sort;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import lombok.experimental.Delegate;

class JpaSort {
  @Delegate
  private final Sort sort;

  public JpaSort(Sort sort) {
    this.sort = sort;
  }

  public <T> Order toOrder(Root<T> root, CriteriaBuilder cb) {
    Path<?> propertyPath = JpaPropertyUtils.getPath(root, getPropertyName());
    return getDirection().equals(Sort.Direction.ASC) ?
        cb.asc(propertyPath) :
        cb.desc(propertyPath);
  }
}
