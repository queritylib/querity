package io.github.queritylib.querity.jpa;

import io.github.queritylib.querity.api.NativeSelectWrapper;

import java.util.Set;

import static io.github.queritylib.querity.common.util.ReflectionUtils.findSubclasses;
import static io.github.queritylib.querity.common.util.SelectUtils.getSelectImplementation;

/**
 * Abstract wrapper for native JPA select implementations.
 */
public abstract class JpaNativeSelectWrapper<T> implements JpaSelect {

  private static final Set<Class<? extends JpaSelect>> JPA_SELECT_IMPLEMENTATIONS = findSubclasses(JpaSelect.class);

  protected final NativeSelectWrapper<T> nativeSelectWrapper;

  protected JpaNativeSelectWrapper(NativeSelectWrapper<T> nativeSelectWrapper) {
    this.nativeSelectWrapper = nativeSelectWrapper;
  }

  public static JpaSelect of(NativeSelectWrapper<?> nativeSelectWrapper) {
    return getSelectImplementation(JPA_SELECT_IMPLEMENTATIONS, nativeSelectWrapper)
      .orElseThrow(() -> new IllegalArgumentException(
        "No JpaSelect implementation found for NativeSelectWrapper with type: " +
          (nativeSelectWrapper.getNativeSelections().isEmpty() ? "empty" :
            nativeSelectWrapper.getNativeSelections().get(0).getClass().getName())));
  }
}
