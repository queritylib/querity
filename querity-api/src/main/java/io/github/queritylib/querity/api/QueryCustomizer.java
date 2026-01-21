package io.github.queritylib.querity.api;

/**
 * Functional interface for customizing native query objects.
 *
 * <p>This interface allows backend-specific customizations to be applied to queries
 * without polluting the generic querity API. Customizers are only applied when the
 * backend implementation supports them.
 *
 * <h2>Example Usage</h2>
 * <pre>{@code
 * // JPA-specific customization
 * QueryCustomizer<TypedQuery<?>> fetchJoinCustomizer = query -> {
 *     EntityGraph<?> graph = entityManager.createEntityGraph(Person.class);
 *     graph.addAttributeNodes("orders", "customer");
 *     query.setHint("jakarta.persistence.fetchgraph", graph);
 * };
 *
 * Query query = Querity.query()
 *     .filter(filterBy("status", "ACTIVE"))
 *     .customize(fetchJoinCustomizer)
 *     .build();
 * }</pre>
 *
 * @param <T> the type of the native query object (e.g., TypedQuery for JPA, Bson for MongoDB)
 * @see Query.QueryBuilder#customize(QueryCustomizer...)
 */
@FunctionalInterface
public interface QueryCustomizer<T> {
  /**
   * Customize the native query object.
   *
   * @param nativeQuery the native query object to customize
   */
  void customize(T nativeQuery);
}
