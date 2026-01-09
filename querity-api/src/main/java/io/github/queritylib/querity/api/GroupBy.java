package io.github.queritylib.querity.api;

/**
 * Marker interface for GROUP BY clauses.
 *
 * <p>Implementations of this interface represent different ways to group query results.
 * The most common implementation is {@link SimpleGroupBy}, which groups by property names
 * or expressions.
 *
 * <h2>Usage Examples</h2>
 *
 * <h3>Simple Property Grouping</h3>
 * <pre>{@code
 * Query query = Querity.query()
 *     .select(selectBy(
 *         prop("category"),
 *         sum(prop("amount")).as("totalAmount")
 *     ))
 *     .groupByProperties("category")
 *     .build();
 * }</pre>
 *
 * <h3>Multiple Property Grouping</h3>
 * <pre>{@code
 * Query query = Querity.query()
 *     .select(selectBy(
 *         prop("category"),
 *         prop("region"),
 *         count(prop("id")).as("orderCount")
 *     ))
 *     .groupByProperties("category", "region")
 *     .build();
 * }</pre>
 *
 * <h3>Expression-based Grouping</h3>
 * <pre>{@code
 * Query query = Querity.query()
 *     .select(selectBy(
 *         upper(prop("category")).as("upperCategory"),
 *         sum(prop("amount")).as("totalAmount")
 *     ))
 *     .groupByExpressions(upper(prop("category")))
 *     .build();
 * }</pre>
 *
 * @see SimpleGroupBy
 * @see Query
 */
public interface GroupBy {
}
