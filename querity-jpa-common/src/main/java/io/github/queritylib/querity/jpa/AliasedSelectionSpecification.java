package io.github.queritylib.querity.jpa;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;

/**
 * A SelectionSpecification that wraps another SelectionSpecification and provides an alias.
 *
 * @param <T> the entity type
 */
public class AliasedSelectionSpecification<T> implements SelectionSpecification<T> {

  private final SelectionSpecification<T> delegate;
  private final String alias;

  private AliasedSelectionSpecification(SelectionSpecification<T> delegate, String alias) {
    this.delegate = delegate;
    this.alias = alias;
  }

  /**
   * Creates an aliased selection specification.
   *
   * @param delegate the underlying selection specification
   * @param alias    the alias for the selection
   * @param <T>      the entity type
   * @return an aliased selection specification
   */
  public static <T> AliasedSelectionSpecification<T> of(SelectionSpecification<T> delegate, String alias) {
    return new AliasedSelectionSpecification<>(delegate, alias);
  }

  @Override
  public Selection<?> toSelection(Root<T> root, CriteriaBuilder cb) {
    return delegate.toSelection(root, cb);
  }

  @Override
  public String getAlias() {
    return alias;
  }
}
