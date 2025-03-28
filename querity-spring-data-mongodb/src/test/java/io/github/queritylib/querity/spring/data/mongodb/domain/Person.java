package io.github.queritylib.querity.spring.data.mongodb.domain;

import io.github.queritylib.querity.test.domain.ProductCategory;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Person implements io.github.queritylib.querity.test.domain.Person<String, Address, Location, Order> {
  @Id
  private String id;
  @NonNull
  private String firstName;
  private String lastName;
  @NonNull
  private String email;
  @NonNull
  private Gender gender;
  private LocalDate birthDate;
  @NonNull
  private BigDecimal height;
  @NonNull
  private Integer children;
  private boolean married;
  @NonNull
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
