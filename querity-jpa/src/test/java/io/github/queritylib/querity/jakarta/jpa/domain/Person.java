package io.github.queritylib.querity.jakarta.jpa.domain;

import io.github.queritylib.querity.test.domain.ProductCategory;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Person extends AbstractPersistable<Long> implements io.github.queritylib.querity.test.domain.Person<Long, Address, Location, Order> {
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
  @OneToOne(mappedBy = "person", cascade = CascadeType.ALL)
  private Address address;
  @NonNull
  @OneToMany(mappedBy = "person", cascade = CascadeType.ALL)
  @Builder.Default
  private List<Location> visitedLocations = new ArrayList<>();
  @NonNull
  @Enumerated
  private ProductCategory favouriteProductCategory;
  @NonNull
  @OneToMany(mappedBy = "person", cascade = CascadeType.ALL)
  @Builder.Default
  private List<Order> orders = new ArrayList<>();

  @Override
  public @NonNull String toString() {
    return "Person{" +
           "id='" + getId() + '\'' +
           ", firstName='" + firstName + '\'' +
           ", lastName='" + lastName + '\'' +
           ", birthDate='" + birthDate + '\'' +
           ", city='" + address.getCity() + '\'' +
           '}';
  }
}
