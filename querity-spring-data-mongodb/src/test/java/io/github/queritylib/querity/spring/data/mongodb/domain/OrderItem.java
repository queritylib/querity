package io.github.queritylib.querity.spring.data.mongodb.domain;

import io.github.queritylib.querity.test.domain.ProductCategory;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem implements io.github.queritylib.querity.test.domain.OrderItem {
  @NonNull
  private String sku;
  @NonNull
  private ProductCategory category;
  @NonNull
  private Integer quantity;
  @NonNull
  private BigDecimal unitPrice;
  @NonNull
  private String description;
}
