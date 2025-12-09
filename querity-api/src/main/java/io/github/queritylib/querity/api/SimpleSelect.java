package io.github.queritylib.querity.api;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.util.Arrays;
import java.util.List;

@Builder(toBuilder = true)
@Jacksonized
@Getter
@EqualsAndHashCode
@ToString
public class SimpleSelect implements Select {
  @NonNull
  @Singular
  private List<String> propertyNames;

  public static SimpleSelect of(String... propertyNames) {
    return SimpleSelect.builder()
        .propertyNames(Arrays.asList(propertyNames))
        .build();
  }
}
