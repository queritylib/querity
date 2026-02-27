package io.github.queritylib.querity.spring.data.elasticsearch.domain;

import lombok.*;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentType implements io.github.queritylib.querity.test.domain.DocumentType {
  @NonNull
  @Field(type = FieldType.Keyword)
  private String code;

  @Override
  public String toString() {
    return "DocumentType{" +
      "code='" + code + '\'' +
      '}';
  }
}
