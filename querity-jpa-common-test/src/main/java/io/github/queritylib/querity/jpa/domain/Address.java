package io.github.queritylib.querity.jpa.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address extends AbstractPersistable<Long> implements io.github.queritylib.querity.test.domain.Address {
  @OneToOne
  @NonNull
  private Person person;
  @NonNull
  private String streetAddress;
  @NonNull
  private String city;

  @Override
  public @NonNull String toString() {
    return "Address{" +
           "streetAddress='" + streetAddress + '\'' +
           ", city='" + city + '\'' +
           '}';
  }
}
