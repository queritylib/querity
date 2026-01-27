package io.github.queritylib.querity.jpa;

import io.github.queritylib.querity.api.QueryCustomizer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

final class JpaQueryCustomizerApplier {
  private JpaQueryCustomizerApplier() {
    throw new UnsupportedOperationException("Utility class");
  }

  static <T> void apply(EntityManager entityManager,
                        Class<T> entityClass,
                        TypedQuery<?> typedQuery,
                        List<QueryCustomizer<?>> customizers) {
    if (customizers == null || customizers.isEmpty()) {
      return;
    }

    JpaQueryContext<T> context = new JpaQueryContext<>(entityManager, entityClass, typedQuery);

    for (QueryCustomizer<?> customizer : customizers) {
      try {
        @SuppressWarnings("unchecked")
        QueryCustomizer<JpaQueryContext<?>> jpaCustomizer = (QueryCustomizer<JpaQueryContext<?>>) customizer;
        jpaCustomizer.customize(context);
      } catch (ClassCastException e) {
        // Ignore customizers that are not for JPA
      }
    }
  }
}
