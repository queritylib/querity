package io.github.queritylib.querity.jpa;

import io.github.queritylib.querity.api.PropertyExpression;
import io.github.queritylib.querity.api.SimpleSelect;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;
import jakarta.persistence.metamodel.Metamodel;

import java.util.ArrayList;
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
    if (simpleSelect.hasExpressions()) {
      return toExpressionsSelections(metamodel, root, cb);
    }
    return toPropertyNamesSelections(metamodel, root);
  }

  private List<Selection<?>> toPropertyNamesSelections(Metamodel metamodel, Root<?> root) {
    return simpleSelect.getPropertyNames().stream()
        .map(propertyName -> {
          Path<?> path = JpaPropertyUtils.getPath(root, propertyName, metamodel);
          return path.alias(propertyName);
        })
        .<Selection<?>>map(p -> p)
        .toList();
  }

  private List<Selection<?>> toExpressionsSelections(Metamodel metamodel, Root<?> root, CriteriaBuilder cb) {
    List<Selection<?>> selections = new ArrayList<>();
    List<PropertyExpression> expressions = simpleSelect.getExpressions();
    List<String> aliases = simpleSelect.getAliasNames();

    for (int i = 0; i < expressions.size(); i++) {
      PropertyExpression expr = expressions.get(i);
      String alias = aliases.get(i);
      Expression<?> jpaExpr = JpaFunctionMapper.toExpression(expr, root, cb, metamodel);
      selections.add(jpaExpr.alias(alias));
    }

    return selections;
  }

  @Override
  public List<String> getPropertyNames() {
    if (simpleSelect.hasExpressions()) {
      return simpleSelect.getAliasNames();
    }
    return simpleSelect.getPropertyNames();
  }
}
