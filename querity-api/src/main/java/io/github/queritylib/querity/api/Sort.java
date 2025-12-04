package io.github.queritylib.querity.api;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Marker interface for sort options.
 * Implementations include {@link SimpleSort} for property-based sorting
 * and {@link NativeSortWrapper} for native sorting.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
    @JsonSubTypes.Type(SimpleSort.class),
    @JsonSubTypes.Type(NativeSortWrapper.class)
})
public interface Sort {
}

