package io.github.queritylib.querity.spring.data.elasticsearch.domain;

import io.github.queritylib.querity.test.domain.ProductCategory;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Document(indexName = "people")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Person implements io.github.queritylib.querity.test.domain.Person<String, Address, Location, Order> {
  @Id
  @Field(type = FieldType.Keyword)
  private String id;
  @NonNull
  @Field(type = FieldType.Keyword)
  private String firstName;
  @Field(type = FieldType.Keyword)
  private String lastName;
  @NonNull
  private String email;
  @NonNull
  private Gender gender;
  @Field(type = FieldType.Date, format = {}, pattern = "uuuu-MM-dd")
  private LocalDate birthDate;
  @NonNull
  private BigDecimal height;
  @NonNull
  private Integer children;
  private boolean married;
  @NonNull
  @Field(type = FieldType.Object)
  private Address address;
  @NonNull
  @Builder.Default
  private List<Location> visitedLocations = new ArrayList<>();
  @NonNull
  private ProductCategory favouriteProductCategory;
  @NonNull
  @Builder.Default
  private List<Order> orders = new ArrayList<>();

  @Override
  public @NonNull String toString() {
    return "Person{" +
           "id='" + id + '\'' +
           ", firstName='" + firstName + '\'' +
           ", lastName='" + lastName + '\'' +
           ", birthDate='" + birthDate + '\'' +
           ", city='" + address.getCity() + '\'' +
           '}';
  }
}
