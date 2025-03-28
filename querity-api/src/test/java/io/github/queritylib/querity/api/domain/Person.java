package io.github.queritylib.querity.api.domain;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class Person {
  @NonNull
  private String firstName;
  @NonNull
  private String lastName;
}
