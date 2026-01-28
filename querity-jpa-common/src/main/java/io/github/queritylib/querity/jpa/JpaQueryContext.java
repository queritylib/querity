package io.github.queritylib.querity.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

/**
 * Context object that provides JPA-specific query information to customizers.
 *
 * <p>This class encapsulates the EntityManager, entity class, and TypedQuery,
 * allowing {@link io.github.queritylib.querity.api.QueryCustomizer} instances
 * to apply JPA-specific hints and configurations.
 *
 * @param <T> the entity type
 */
public class JpaQueryContext<T> {
  private final EntityManager entityManager;
  private final Class<T> entityClass;
  private final TypedQuery<?> typedQuery;

  public JpaQueryContext(EntityManager entityManager, Class<T> entityClass, TypedQuery<?> typedQuery) {
    this.entityManager = entityManager;
    this.entityClass = entityClass;
    this.typedQuery = typedQuery;
  }

  public EntityManager getEntityManager() {
    return entityManager;
  }

  public Class<T> getEntityClass() {
    return entityClass;
  }

  public TypedQuery<?> getTypedQuery() {
    return typedQuery;
  }
}
