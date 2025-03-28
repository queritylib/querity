package io.github.queritylib.querity.api;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

@Builder(toBuilder = true)
@Jacksonized
@Getter
@EqualsAndHashCode
@ToString
public class Sort {
  @NonNull
  private String propertyName;
  @Builder.Default
  @NonNull
  private Direction direction = Direction.ASC;

  public enum Direction {
    ASC, DESC
  }
}
