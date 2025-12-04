package io.github.queritylib.querity.api;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.jackson.Jacksonized;

@Builder(toBuilder = true)
@Jacksonized
@Getter
@EqualsAndHashCode
public class NativeSortWrapper<T> implements Sort {
  @NonNull
  private T nativeSort;
}

