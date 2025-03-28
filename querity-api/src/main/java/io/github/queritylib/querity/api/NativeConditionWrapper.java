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
public class NativeConditionWrapper<T> implements Condition {
  @NonNull
  private T nativeCondition;
}
