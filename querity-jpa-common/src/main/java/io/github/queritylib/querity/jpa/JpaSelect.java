package io.github.queritylib.querity.jpa;

import io.github.queritylib.querity.api.Select;
import io.github.queritylib.querity.api.SimpleSelect;
import jakarta.persistence.criteria.CriteriaBuilder;
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
   * @param cb        the criteria builder
   * @return a list of JPA selections
   */
  List<Selection<?>> toSelections(Metamodel metamodel, Root<?> root, CriteriaQuery<?> cq, CriteriaBuilder cb);

  /**
   * Get the property names for this select.
   *
   * @return list of property names
   */
  List<String> getPropertyNames();

  /**
   * Create a JpaSelect from an API Select.
   */
  static JpaSelect of(Select select) {
    if (select instanceof SimpleSelect simpleSelect) {
      return new JpaSimpleSelect(simpleSelect);
    }
    throw new IllegalArgumentException("Unsupported select type: " + select.getClass().getSimpleName());
  }
}
