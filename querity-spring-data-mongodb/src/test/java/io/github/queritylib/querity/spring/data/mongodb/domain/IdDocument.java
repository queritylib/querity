package io.github.queritylib.querity.spring.data.mongodb.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdDocument implements io.github.queritylib.querity.test.domain.IdDocument<DocumentType> {
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
