package io.github.queritylib.querity.spring.data.elasticsearch.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdDocument implements io.github.queritylib.querity.test.domain.IdDocument<DocumentType> {
  @Field(type = FieldType.Object)
  private DocumentType type;
  @Field(type = FieldType.Keyword)
  private String number;

  @Override
  public String toString() {
    return "IdDocument{" +
      "type=" + type +
      ", number='" + number + '\'' +
      '}';
  }
}
