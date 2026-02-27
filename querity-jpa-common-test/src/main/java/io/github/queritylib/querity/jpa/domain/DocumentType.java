package io.github.queritylib.querity.jpa.domain;

import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentType extends AbstractPersistable<Long> implements io.github.queritylib.querity.test.domain.DocumentType {
  @NonNull
  private String code;

  @Override
  public String toString() {
    return "DocumentType{" +
      "code='" + code + '\'' +
      '}';
  }
}
