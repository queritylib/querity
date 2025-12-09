package io.github.queritylib.querity.api;

import java.util.List;

/**
 * Interface for select options.
 * Implementations include {@link SimpleSelect} for property-based selection
 * and {@link NativeSelectWrapper} for native selection.
 */
public interface Select {
  /**
   * Get the property names to select.
   * @return list of property names
   */
  List<String> getPropertyNames();
}
