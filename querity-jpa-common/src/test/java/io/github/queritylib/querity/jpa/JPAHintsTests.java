package io.github.queritylib.querity.jpa;

import io.github.queritylib.querity.api.QueryCustomizer;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Subgraph;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JPAHintsTests {

  @Mock private EntityManager entityManager;
  @Mock private TypedQuery<?> typedQuery;
  @Mock private EntityGraph<?> entityGraph;
  @Mock private Subgraph<?> subgraph;

  private JpaQueryContext<?> context;

  @BeforeEach
  void setUp() {
    context = new JpaQueryContext<>(entityManager, Object.class, typedQuery);
  }

  @Test
  @SuppressWarnings("unchecked")
  void testFetchJoinSimple() {
    when(entityManager.createEntityGraph(any(Class.class))).thenReturn((EntityGraph) entityGraph);

    QueryCustomizer<JpaQueryContext<?>> customizer = JPAHints.fetchJoin("field1");
    customizer.customize(context);

    verify(entityGraph).addAttributeNodes("field1");
    verify(typedQuery).setHint("jakarta.persistence.loadgraph", entityGraph);
  }

  @Test
  @SuppressWarnings("unchecked")
  void testFetchJoinNested() {
    when(entityManager.createEntityGraph(any(Class.class))).thenReturn((EntityGraph) entityGraph);
    when(entityGraph.addSubgraph(any(String.class))).thenReturn((Subgraph) subgraph);

    QueryCustomizer<JpaQueryContext<?>> customizer = JPAHints.fetchJoin("field1.field2");
    customizer.customize(context);

    verify(entityGraph).addAttributeNodes("field1");
    verify(entityGraph).addSubgraph("field1");
    verify(subgraph).addAttributeNodes("field2");
    verify(typedQuery).setHint("jakarta.persistence.loadgraph", entityGraph);
  }

  @Test
  void testBatchSize() {
    JPAHints.batchSize(50).customize(context);
    verify(typedQuery).setHint("org.hibernate.fetchSize", 50);
  }

  @Test
  void testTimeout() {
    JPAHints.timeout(1000).customize(context);
    verify(typedQuery).setHint("jakarta.persistence.query.timeout", 1000);
  }

  @Test
  void testCacheable() {
    JPAHints.cacheable(true).customize(context);
    verify(typedQuery).setHint("org.hibernate.cacheable", true);
  }

  @Test
  void testHint() {
    JPAHints.hint("my.hint", "value").customize(context);
    verify(typedQuery).setHint("my.hint", "value");
  }

  @Test
  void testConstructorIsPrivate() throws Exception {
    java.lang.reflect.Constructor<JPAHints> constructor = JPAHints.class.getDeclaredConstructor();
    org.junit.jupiter.api.Assertions.assertTrue(java.lang.reflect.Modifier.isPrivate(constructor.getModifiers()));
    constructor.setAccessible(true);
    org.junit.jupiter.api.Assertions.assertThrows(java.lang.reflect.InvocationTargetException.class, constructor::newInstance);
  }

  @Test
  @SuppressWarnings("unchecked")
  void testNamedEntityGraph() {
    when(entityManager.getEntityGraph("Person.withAddress")).thenReturn((EntityGraph) entityGraph);

    JPAHints.namedEntityGraph("Person.withAddress").customize(context);

    verify(entityManager).getEntityGraph("Person.withAddress");
    verify(typedQuery).setHint("jakarta.persistence.loadgraph", entityGraph);
  }
}
