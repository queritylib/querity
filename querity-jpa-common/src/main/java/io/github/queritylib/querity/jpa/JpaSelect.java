package io.github.queritylib.querity.jpa;

import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;
import jakarta.persistence.metamodel.Metamodel;

import java.util.List;

/**
 * Interface for JPA select implementations.
 */
public interface JpaSelect {

  /**
   * Convert this select to a list of JPA Selections.
   *
   * @param metamodel the JPA metamodel
   * @param root      the root of the query
   * @param cq        the criteria query
   * @return a list of JPA selections
   */
  List<Selection<?>> toSelections(Metamodel metamodel, Root<?> root, CriteriaQuery<?> cq);

  /**
   * Get the property names for this select.
   *
   * @return list of property names
   */
  List<String> getPropertyNames();

  /**
   * Create a JpaSelect from an API SimpleSelect.
   */
  static JpaSelect of(io.github.queritylib.querity.api.SimpleSelect select) {
    return new JpaSimpleSelect(select);
  }
}
