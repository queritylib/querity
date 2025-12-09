package io.github.queritylib.querity.jpa;

import io.github.queritylib.querity.api.NativeSelectWrapper;
import io.github.queritylib.querity.common.util.SelectUtils;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;
import jakarta.persistence.metamodel.Metamodel;
import org.reflections.Reflections;

import java.util.List;
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

  @Override
  public abstract List<Selection<?>> toSelections(Metamodel metamodel, Root<?> root, CriteriaQuery<?> cq, CriteriaBuilder cb);

  @Override
  public abstract List<String> getPropertyNames();
}
