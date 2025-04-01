package io.github.queritylib.querity.jpa;

import io.github.queritylib.querity.api.Condition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class JpaConditionTests {
  @Test
  void givenNotSupportedCondition_whenOf_theThrowIllegalArgumentException() {
    Condition condition = new MyCondition();
    assertThrows(IllegalArgumentException.class, () -> JpaCondition.of(condition),
        "Condition class MyCondition is not supported by the JPA module");
  }

  private static class MyCondition implements Condition {
  }
}
