package io.github.queritylib.querity.spring.data.mongodb.domain;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location implements io.github.queritylib.querity.test.domain.Location {
  @NonNull
  private String country;
  @NonNull
  @Builder.Default
  private List<String> cities = new ArrayList<>();

  @Override
  public @NonNull String toString() {
    return "Location{" +
        "country='" + country + '\'' +
        '}';
  }
}
