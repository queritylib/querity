package io.github.queritylib.querity.spring.data.jpa.domain;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.*;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location extends AbstractPersistable<Long> implements io.github.queritylib.querity.test.domain.Location {
  @ManyToOne
  @NonNull
  private Person person;
  @NonNull
  private String country;
  @NonNull
  @ElementCollection
  @Builder.Default
  private List<String> cities = new ArrayList<>();

  @Override
  public @NonNull String toString() {
    return "Location{" +
        "country='" + country + '\'' +
        "} " + super.toString();
  }
}
