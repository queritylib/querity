package io.github.queritylib.querity.jpa.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdDocument implements io.github.queritylib.querity.test.domain.IdDocument<DocumentType> {
  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private DocumentType type;
  private String number;

  @Override
  public String toString() {
    return "IdDocument{" +
      "type=" + type +
      ", number='" + number + '\'' +
      '}';
  }
}
