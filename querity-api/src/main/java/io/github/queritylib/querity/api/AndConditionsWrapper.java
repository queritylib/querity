package io.github.queritylib.querity.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

public class AndConditionsWrapper extends LogicConditionsWrapper {
  @Builder(toBuilder = true)
  @Jacksonized
  public AndConditionsWrapper(List<Condition> conditions) {
    super(LogicOperator.AND, conditions);
  }

  @Override
  @JsonProperty("and")
  public @NonNull List<Condition> getConditions() {
    return super.getConditions();
  }
}
