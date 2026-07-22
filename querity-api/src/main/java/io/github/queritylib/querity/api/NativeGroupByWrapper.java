package io.github.queritylib.querity.api;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * Wraps native GROUP BY groupings, allowing to group query results by expressions
 * the querity model cannot express (e.g. a JSON-extraction over a jsonb column).
 *
 * <p>Mirrors {@link NativeSelectWrapper} and {@link NativeSortWrapper}: the element type is
 * backend-specific — for the JPA modules use {@code GroupBySpecification} for deferred grouping
 * expressions built from the query {@code Root} and {@code CriteriaBuilder} at execution time.
 *
 * <p>Like the other native wrappers, instances are meant to be built programmatically — native
 * groupings are backend objects or lambdas that have no meaningful JSON representation.
 *
 * @param <T> the backend-specific native grouping type
 * @see Querity#groupByNative(Object...)
 * @see GroupBy
 */
@Builder(toBuilder = true)
@Jacksonized
@Getter
@EqualsAndHashCode
@ToString
public class NativeGroupByWrapper<T> implements GroupBy {
  @NonNull
  @Singular
  private List<T> nativeGroupings;
}
