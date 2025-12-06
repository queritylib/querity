package io.github.queritylib.querity.jpa;

import io.github.queritylib.querity.api.NativeSelectWrapper;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;
import jakarta.persistence.metamodel.Metamodel;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA-specific native select wrapper that supports {@link SelectionSpecification}.
 * This allows users to create native JPA selections using a lambda that receives
 * the Root and CriteriaBuilder at query execution time.
 */
public class JpaSelectionSpecificationSelectWrapper extends JpaNativeSelectWrapper<SelectionSpecification<?>> {

  public JpaSelectionSpecificationSelectWrapper(NativeSelectWrapper<SelectionSpecification<?>> nativeSelectWrapper) {
    super(nativeSelectWrapper);
  }

  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public List<Selection<?>> toSelections(Metamodel metamodel, Root<?> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
    List<Selection<?>> selections = new ArrayList<>();
    for (SelectionSpecification<?> spec : nativeSelectWrapper.getNativeSelections()) {
      Selection<?> selection = ((SelectionSpecification) spec).toSelection(root, cb);
      String alias = spec.getAlias();
      if (alias != null) {
        selection = selection.alias(alias);
      }
      selections.add(selection);
    }
    return selections;
  }

  @Override
  public List<String> getPropertyNames() {
    List<String> names = new ArrayList<>();
    for (SelectionSpecification<?> spec : nativeSelectWrapper.getNativeSelections()) {
      String alias = spec.getAlias();
      if (alias != null) {
        names.add(alias);
      }
    }
    return names;
  }
}
