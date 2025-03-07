package net.brunomendola.querity.spring.data.elasticsearch.domain;

import lombok.*;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address implements net.brunomendola.querity.test.domain.Address {
  private String streetAddress;
  @Field(type = FieldType.Keyword)
  private String city;

  @Override
  public @NonNull String toString() {
    return "Address{" +
           "streetAddress='" + streetAddress + '\'' +
           ", city='" + city + '\'' +
           '}';
  }
}
