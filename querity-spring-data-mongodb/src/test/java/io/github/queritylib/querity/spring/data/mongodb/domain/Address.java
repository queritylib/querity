package io.github.queritylib.querity.spring.data.mongodb.domain;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address implements io.github.queritylib.querity.test.domain.Address {
  private String streetAddress;
  private String city;

  @Override
  public @NonNull String toString() {
    return "Address{" +
        "streetAddress='" + streetAddress + '\'' +
        ", city='" + city + '\'' +
        '}';
  }
}
