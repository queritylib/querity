package io.github.queritylib.querity.jakarta.jpa;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import javax.annotation.Nullable;
import java.util.Objects;

@FunctionalInterface
public interface Specification<T> {
  @Nullable
  Predicate toPredicate(Root<T> root, CriteriaQuery<?> cq, CriteriaBuilder cb);

  static <T> Specification<T> where(@Nullable Specification<T> spec) {
    return Objects.requireNonNullElseGet(spec,
        () -> (root, cq, cb) -> null);
  }
}
