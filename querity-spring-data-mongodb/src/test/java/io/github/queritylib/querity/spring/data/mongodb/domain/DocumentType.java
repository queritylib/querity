package io.github.queritylib.querity.spring.data.mongodb.domain;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentType implements io.github.queritylib.querity.test.domain.DocumentType {
  @NonNull
  private String code;

  @Override
  public String toString() {
    return "DocumentType{" +
      "code='" + code + '\'' +
      '}';
  }
}
