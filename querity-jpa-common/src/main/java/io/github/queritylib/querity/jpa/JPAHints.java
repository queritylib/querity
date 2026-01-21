package io.github.queritylib.querity.jpa;

import io.github.queritylib.querity.api.QueryCustomizer;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Subgraph;
import jakarta.persistence.TypedQuery;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Utility class for creating common JPA query customizers.
 *
 * <p>This class provides factory methods for creating {@link QueryCustomizer} instances
 * that apply JPA-specific hints and optimizations such as fetch joins, batch sizes, and timeouts.
 *
 * <p><b>Example Usage</b></p>
 * <pre>{@code
 * Query query = Querity.query()
 *     .filter(filterBy("status", "ACTIVE"))
 *     .customize(
 *         JPAHints.fetchJoin("orders", "customer"),
 *         JPAHints.batchSize(50)
 *     )
 *     .build();
 *
 * List<Person> results = querity.findAll(Person.class, query);
 * }</pre>
 */
public final class JPAHints {

  private static final String HINT_LOADGRAPH = "jakarta.persistence.loadgraph";
  private static final String HINT_FETCH_SIZE = "org.hibernate.fetchSize";
  private static final String HINT_TIMEOUT = "jakarta.persistence.query.timeout";
  private static final String HINT_CACHEABLE = "org.hibernate.cacheable";

  private JPAHints() {
    throw new UnsupportedOperationException("Utility class");
  }

  /**
   * Create a customizer that applies JOIN FETCH hints for the specified entity paths.
   *
   * <p>This customizer uses the {@code jakarta.persistence.loadgraph} hint to eagerly load
   * the specified associations.
   *
   * <p><b>Note:</b> This hint is <b>additive</b>. It adds the specified paths to the fetch plan,
   * but respects the static {@code FetchType.EAGER} configuration of the entity.
   * Attributes configured as EAGER in the entity will still be fetched eagerly.
   *
   * <p><b>Important:</b> This customizer requires access to the {@link EntityManager} and the
   * entity class at runtime. It will be applied by the JPA implementation layer.
   *
   * <p><b>Example:</b></p>
   * <pre>{@code
   * // Fetch joins for Person.orders and Person.address
   * Query query = Querity.query()
   *     .filter(filterBy("status", "ACTIVE"))
   *     .customize(JPAHints.fetchJoin("orders", "address"))
   *     .build();
   * }</pre>
   *
   * <p><b>Nested paths:</b> You can use dot notation for nested associations:
   * <pre>{@code
   * // Fetch Person.orders and also Order.items
   * .customize(JPAHints.fetchJoin("orders", "orders.items"))
   * }</pre>
   *
   * @param paths the entity attribute paths to fetch join (supports nested paths with dot notation)
   * @return a QueryCustomizer that applies fetch join hints
   */
  public static QueryCustomizer<JpaQueryContext<?>> fetchJoin(String... paths) {
    Objects.requireNonNull(paths, "Paths cannot be null");
    return context -> {
      EntityGraph<?> graph = context.getEntityManager().createEntityGraph(context.getEntityClass());
      Map<String, Subgraph<?>> subgraphs = new HashMap<>();

      for (String path : paths) {
        applyPathToGraph(graph, subgraphs, path);
      }

      context.getTypedQuery().setHint(HINT_LOADGRAPH, graph);
    };
  }

  /**
   * Create a customizer that uses a named EntityGraph defined on the entity.
   *
   * <p>This customizer uses the {@code jakarta.persistence.loadgraph} hint to apply a
   * {@link jakarta.persistence.NamedEntityGraph} that has been statically defined on the entity class.
   *
   * <p><b>Example:</b></p>
   * <pre>{@code
   * // Assuming Person entity has @NamedEntityGraph(name = "Person.withAddress", ...)
   * Query query = Querity.query()
   *     .customize(JPAHints.namedEntityGraph("Person.withAddress"))
   *     .build();
   * }</pre>
   *
   * @param graphName the name of the EntityGraph to use
   * @return a QueryCustomizer that applies the named entity graph
   */
  public static QueryCustomizer<JpaQueryContext<?>> namedEntityGraph(String graphName) {
    Objects.requireNonNull(graphName, "Graph name cannot be null");
    return context -> {
      EntityGraph<?> graph = context.getEntityManager().getEntityGraph(graphName);
      context.getTypedQuery().setHint(HINT_LOADGRAPH, graph);
    };
  }

  /**
   * Helper method to apply a path to an EntityGraph, supporting nested paths.
   */
  private static void applyPathToGraph(EntityGraph<?> graph, Map<String, Subgraph<?>> subgraphs, String path) {
    String[] parts = path.split("\\.");

    if (parts.length == 1) {
      // Simple attribute
      graph.addAttributeNodes(path);
    } else {
      // Nested path: first part is the root attribute, rest is the nested path
      String rootAttribute = parts[0];
      String nestedPath = path.substring(rootAttribute.length() + 1);

      // Get or create subgraph for root attribute
      Subgraph<?> subgraph = subgraphs.computeIfAbsent(rootAttribute, attr -> {
        graph.addAttributeNodes(attr);
        return graph.addSubgraph(attr);
      });

      // Recursively apply nested path to subgraph
      applyNestedPathToSubgraph(subgraph, subgraphs, rootAttribute, nestedPath);
    }
  }

  /**
   * Helper method to recursively apply nested paths to subgraphs.
   */
  private static void applyNestedPathToSubgraph(
      Subgraph<?> parentSubgraph,
      Map<String, Subgraph<?>> subgraphs,
      String parentPath,
      String nestedPath) {

    String[] parts = nestedPath.split("\\.", 2);
    String currentAttribute = parts[0];
    String fullPath = parentPath + "." + currentAttribute;

    if (parts.length == 1) {
      // Leaf attribute in the nested path
      parentSubgraph.addAttributeNodes(currentAttribute);
    } else {
      // More nesting
      String remainingPath = parts[1];
      Subgraph<?> subgraph = subgraphs.computeIfAbsent(fullPath, key -> {
        parentSubgraph.addAttributeNodes(currentAttribute);
        return parentSubgraph.addSubgraph(currentAttribute);
      });
      applyNestedPathToSubgraph(subgraph, subgraphs, fullPath, remainingPath);
    }
  }

  /**
   * Create a customizer that sets the Hibernate fetch size (batch size) hint.
   *
   * <p>This hint controls how many rows are fetched in a single database round-trip.
   *
   * <p><b>Example:</b></p>
   * <pre>{@code
   * Query query = Querity.query()
   *     .filter(filterBy("status", "ACTIVE"))
   *     .customize(JPAHints.batchSize(100))
   *     .build();
   * }</pre>
   *
   * @param size the batch size (number of rows to fetch per round-trip)
   * @return a QueryCustomizer that sets the batch size hint
   */
  public static QueryCustomizer<JpaQueryContext<?>> batchSize(int size) {
    return context -> context.getTypedQuery().setHint(HINT_FETCH_SIZE, size);
  }

  /**
   * Create a customizer that sets the query timeout in milliseconds.
   *
   * <p><b>Example:</b></p>
   * <pre>{@code
   * Query query = Querity.query()
   *     .filter(filterBy("status", "ACTIVE"))
   *     .customize(JPAHints.timeout(5000)) // 5 seconds
   *     .build();
   * }</pre>
   *
   * @param milliseconds the timeout in milliseconds
   * @return a QueryCustomizer that sets the query timeout
   */
  public static QueryCustomizer<JpaQueryContext<?>> timeout(int milliseconds) {
    return context -> context.getTypedQuery().setHint(HINT_TIMEOUT, milliseconds);
  }

  /**
   * Create a customizer that enables or disables the query cache.
   *
   * <p><b>Example:</b></p>
   * <pre>{@code
   * Query query = Querity.query()
   *     .filter(filterBy("status", "ACTIVE"))
   *     .customize(JPAHints.cacheable(true))
   *     .build();
   * }</pre>
   *
   * @param cacheable true to enable query caching, false to disable
   * @return a QueryCustomizer that sets the cacheable hint
   */
  public static QueryCustomizer<JpaQueryContext<?>> cacheable(boolean cacheable) {
    return context -> context.getTypedQuery().setHint(HINT_CACHEABLE, cacheable);
  }

  /**
   * Create a customizer that sets a generic JPA hint.
   *
   * <p>Use this for provider-specific or less common hints not covered by the other methods.
   *
   * <p><b>Example:</b></p>
   * <pre>{@code
   * Query query = Querity.query()
   *     .customize(JPAHints.hint("org.hibernate.readOnly", true))
   *     .build();
   * }</pre>
   *
   * @param name the hint name
   * @param value the hint value
   * @return a QueryCustomizer that sets the specified hint
   */
  public static QueryCustomizer<JpaQueryContext<?>> hint(String name, Object value) {
    Objects.requireNonNull(name, "Hint name cannot be null");
    return context -> context.getTypedQuery().setHint(name, value);
  }
}
