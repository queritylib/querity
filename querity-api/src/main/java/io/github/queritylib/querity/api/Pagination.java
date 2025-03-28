package io.github.queritylib.querity.api;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@Getter
@EqualsAndHashCode
@ToString
public class Pagination {
  @Builder.Default
  @NonNull
  private Integer page = 1;
  @NonNull
  private Integer pageSize;
}
