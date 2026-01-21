package io.github.queritylib.querity.jpa;

import io.github.queritylib.querity.api.NativeConditionWrapper;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.Metamodel;
import lombok.experimental.Delegate;
import org.springframework.data.jpa.domain.Specification;

class JpaNativeConditionWrapper extends JpaCondition {
  private final NativeConditionWrapper<Specification<?>> nativeConditionWrapper;

  JpaNativeConditionWrapper(NativeConditionWrapper<Specification<?>> nativeConditionWrapper) {
    this.nativeConditionWrapper = nativeConditionWrapper;
  }

  public Specification<?> getNativeCondition() {
    return nativeConditionWrapper.getNativeCondition();
  }

  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public <T> Predicate toPredicate(Class<T> entityClass, Metamodel metamodel, Root<T> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
    return getNativeCondition().toPredicate((Root) root, cq, cb);
  }
}
