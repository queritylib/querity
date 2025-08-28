package io.github.queritylib.querity.spring.data.elasticsearch.domain;

import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order implements io.github.queritylib.querity.test.domain.Order<OrderItem> {
  @NonNull
  private Short year;
  @NonNull
  private Integer number;
  @NonNull
  @Builder.Default
  private List<OrderItem> items = new ArrayList<>();
  private UUID externalId;
}
