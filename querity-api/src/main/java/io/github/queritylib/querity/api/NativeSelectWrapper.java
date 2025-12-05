package io.github.queritylib.querity.api;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.util.Collections;
import java.util.List;

@Builder(toBuilder = true)
@Jacksonized
@Getter
@EqualsAndHashCode
@ToString
public class NativeSelectWrapper<T> implements Select {
  @NonNull
  @Singular
  private List<T> nativeSelections;

  @Override
  public List<String> getPropertyNames() {
    // Native selects don't have simple property names
    return Collections.emptyList();
  }
}
