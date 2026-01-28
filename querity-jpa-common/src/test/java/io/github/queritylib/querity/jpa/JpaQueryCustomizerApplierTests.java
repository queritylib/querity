package io.github.queritylib.querity.jpa;

import io.github.queritylib.querity.api.QueryCustomizer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class JpaQueryCustomizerApplierTests {

  @Mock private EntityManager entityManager;
  @Mock private TypedQuery<?> typedQuery;

  @Test
  void givenNullCustomizers_whenApply_thenDoNothing() {
    JpaQueryCustomizerApplier.apply(entityManager, Object.class, typedQuery, null);
    verifyNoInteractions(typedQuery);
  }

  @Test
  void givenEmptyCustomizers_whenApply_thenDoNothing() {
    JpaQueryCustomizerApplier.apply(entityManager, Object.class, typedQuery, Collections.emptyList());
    verifyNoInteractions(typedQuery);
  }

  @Test
  void givenCustomizer_whenApply_thenInvokeCustomizerWithContext() {
    AtomicReference<JpaQueryContext<?>> contextRef = new AtomicReference<>();
    QueryCustomizer<JpaQueryContext<?>> customizer = contextRef::set;

    JpaQueryCustomizerApplier.apply(entityManager, Object.class, typedQuery, Collections.singletonList(customizer));

    assertThat(contextRef.get()).isNotNull();
    assertThat(contextRef.get().getEntityManager()).isSameAs(entityManager);
    assertThat(contextRef.get().getEntityClass()).isEqualTo(Object.class);
    assertThat(contextRef.get().getTypedQuery()).isSameAs(typedQuery);
  }

  @Test
  void constructorIsPrivate() throws Exception {
    Constructor<JpaQueryCustomizerApplier> constructor = JpaQueryCustomizerApplier.class.getDeclaredConstructor();
    assertTrue(java.lang.reflect.Modifier.isPrivate(constructor.getModifiers()));
    constructor.setAccessible(true);
    assertThrows(InvocationTargetException.class, constructor::newInstance);
  }
}
