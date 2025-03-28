package io.github.queritylib.querity.test.domain;

import java.math.BigDecimal;

public interface OrderItem {
  String getSku();

  void setSku(String sku);

  ProductCategory getCategory();

  void setCategory(ProductCategory category);

  Integer getQuantity();

  void setQuantity(Integer quantity);

  BigDecimal getUnitPrice();

  void setUnitPrice(BigDecimal unitPrice);

  String getDescription();

  void setDescription(String description);
}
