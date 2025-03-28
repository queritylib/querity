package io.github.queritylib.querity.api;

public interface Condition {
  default boolean isEmpty() {
    return false;
  }
}
