package io.github.queritylib.querity.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.List;

@Getter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
public abstract class LogicConditionsWrapper implements Condition {
  @NonNull
  @JsonIgnore
  protected final LogicOperator logic;
  @NonNull
  protected final List<Condition> conditions;

  @Override
  public boolean isEmpty() {
    return this.conditions.isEmpty();
  }

}
