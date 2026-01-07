package io.github.queritylib.querity.jpa;

import io.github.queritylib.querity.api.NativeSelectWrapper;
import io.github.queritylib.querity.common.util.SelectUtils;
import org.reflections.Reflections;

import java.util.Set;

/**
 * Abstract wrapper for native JPA select implementations.
 */
public abstract class JpaNativeSelectWrapper<T> implements JpaSelect {

  private static Set<Class<? extends JpaSelect>> implementationClasses;

  protected final NativeSelectWrapper<T> nativeSelectWrapper;

  protected JpaNativeSelectWrapper(NativeSelectWrapper<T> nativeSelectWrapper) {
    this.nativeSelectWrapper = nativeSelectWrapper;
  }

  public static JpaSelect of(NativeSelectWrapper<?> nativeSelectWrapper) {
    return SelectUtils.getSelectImplementation(getImplementationClasses(), nativeSelectWrapper)
        .orElseThrow(() -> new IllegalArgumentException(
            "No JpaSelect implementation found for NativeSelectWrapper with type: " +
            (nativeSelectWrapper.getNativeSelections().isEmpty() ? "empty" :
             nativeSelectWrapper.getNativeSelections().get(0).getClass().getName())));
  }

  private static Set<Class<? extends JpaSelect>> getImplementationClasses() {
    if (implementationClasses == null) {
      Reflections reflections = new Reflections("io.github.queritylib.querity");
      implementationClasses = reflections.getSubTypesOf(JpaSelect.class);
    }
    return implementationClasses;
  }

}
