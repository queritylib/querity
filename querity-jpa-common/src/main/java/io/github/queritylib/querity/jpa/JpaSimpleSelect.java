package io.github.queritylib.querity.jpa;

import io.github.queritylib.querity.api.SimpleSelect;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;
import jakarta.persistence.metamodel.Metamodel;

import java.util.List;

/**
 * JPA implementation for SimpleSelect.
 */
public class JpaSimpleSelect implements JpaSelect {

  private final SimpleSelect simpleSelect;

  public JpaSimpleSelect(SimpleSelect simpleSelect) {
    this.simpleSelect = simpleSelect;
  }

  @Override
  public List<Selection<?>> toSelections(Metamodel metamodel, Root<?> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
    return simpleSelect.getPropertyNames().stream()
        .map(propertyName -> {
          Path<?> path = JpaPropertyUtils.getPath(root, propertyName, metamodel);
          return path.alias(propertyName);
        })
        .<Selection<?>>map(p -> p)
        .toList();
  }

  @Override
  public List<String> getPropertyNames() {
    return simpleSelect.getPropertyNames();
  }
}
