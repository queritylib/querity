package io.github.queritylib.querity.jpa;

import io.github.queritylib.querity.api.NativeSelectWrapper;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;
import jakarta.persistence.metamodel.Metamodel;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.github.queritylib.querity.api.Querity.selectByNative;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JpaSelectionSpecificationTests {

  @Nested
  class JpaSelectionSpecificationSelectWrapperTests {
    @SuppressWarnings("unchecked")
    @Test
    void givenSelectionSpecificationWithoutAlias_whenToSelections_thenReturnSelectionsWithoutAlias() {
      Selection<?> mockSelection = mock(Selection.class);
      SelectionSpecification<Object> spec = (root, cb) -> mockSelection;
      NativeSelectWrapper<SelectionSpecification<?>> wrapper = selectByNative(spec);
      JpaSelectionSpecificationSelectWrapper selectWrapper = new JpaSelectionSpecificationSelectWrapper(wrapper);

      Metamodel metamodel = mock(Metamodel.class);
      Root<Object> root = mock(Root.class);
      CriteriaQuery<?> cq = mock(CriteriaQuery.class);
      CriteriaBuilder cb = mock(CriteriaBuilder.class);

      List<Selection<?>> selections = selectWrapper.toSelections(metamodel, root, cq, cb);

      assertThat(selections).hasSize(1);
      assertThat(selections.get(0)).isSameAs(mockSelection);
    }

    @SuppressWarnings("unchecked")
    @Test
    void givenSelectionSpecificationWithAlias_whenToSelections_thenReturnSelectionsWithAlias() {
      Selection<?> mockSelection = mock(Selection.class);
      Selection<?> aliasedSelection = mock(Selection.class);
      when(mockSelection.alias("myAlias")).thenReturn((Selection) aliasedSelection);

      SelectionSpecification<Object> spec = AliasedSelectionSpecification.of(
          (root, cb) -> mockSelection,
          "myAlias"
      );
      NativeSelectWrapper<SelectionSpecification<?>> wrapper = selectByNative(spec);
      JpaSelectionSpecificationSelectWrapper selectWrapper = new JpaSelectionSpecificationSelectWrapper(wrapper);

      Metamodel metamodel = mock(Metamodel.class);
      Root<Object> root = mock(Root.class);
      CriteriaQuery<?> cq = mock(CriteriaQuery.class);
      CriteriaBuilder cb = mock(CriteriaBuilder.class);

      List<Selection<?>> selections = selectWrapper.toSelections(metamodel, root, cq, cb);

      assertThat(selections).hasSize(1);
      assertThat(selections.get(0)).isSameAs(aliasedSelection);
    }

    @Test
    void givenSelectionSpecificationWithoutAlias_whenGetPropertyNames_thenReturnEmptyList() {
      SelectionSpecification<Object> spec = (root, cb) -> root.get("id");
      NativeSelectWrapper<SelectionSpecification<?>> wrapper = selectByNative(spec);
      JpaSelectionSpecificationSelectWrapper selectWrapper = new JpaSelectionSpecificationSelectWrapper(wrapper);

      List<String> propertyNames = selectWrapper.getPropertyNames();

      assertThat(propertyNames).isEmpty();
    }

    @Test
    void givenSelectionSpecificationWithAlias_whenGetPropertyNames_thenReturnAlias() {
      SelectionSpecification<Object> spec = AliasedSelectionSpecification.of(
          (root, cb) -> root.get("id"),
          "customAlias"
      );
      NativeSelectWrapper<SelectionSpecification<?>> wrapper = selectByNative(spec);
      JpaSelectionSpecificationSelectWrapper selectWrapper = new JpaSelectionSpecificationSelectWrapper(wrapper);

      List<String> propertyNames = selectWrapper.getPropertyNames();

      assertThat(propertyNames).containsExactly("customAlias");
    }

    @Test
    void givenMultipleSelectionSpecifications_whenGetPropertyNames_thenReturnOnlyAliases() {
      SelectionSpecification<Object> specWithAlias = AliasedSelectionSpecification.of(
          (root, cb) -> root.get("id"),
          "idAlias"
      );
      SelectionSpecification<Object> specWithoutAlias = (root, cb) -> root.get("name");
      SelectionSpecification<Object> specWithAnotherAlias = AliasedSelectionSpecification.of(
          (root, cb) -> root.get("email"),
          "emailAlias"
      );

      NativeSelectWrapper<SelectionSpecification<?>> wrapper = selectByNative(
          specWithAlias, specWithoutAlias, specWithAnotherAlias
      );
      JpaSelectionSpecificationSelectWrapper selectWrapper = new JpaSelectionSpecificationSelectWrapper(wrapper);

      List<String> propertyNames = selectWrapper.getPropertyNames();

      assertThat(propertyNames).containsExactly("idAlias", "emailAlias");
    }

    @Test
    void givenSelectionSpecification_whenJpaNativeSelectWrapperOf_thenReturnJpaSelectionSpecificationSelectWrapper() {
      SelectionSpecification<Object> spec = (root, cb) -> root.get("id");
      NativeSelectWrapper<SelectionSpecification<?>> wrapper = selectByNative(spec);

      JpaSelect jpaSelect = JpaNativeSelectWrapper.of(wrapper);

      assertThat(jpaSelect).isInstanceOf(JpaSelectionSpecificationSelectWrapper.class);
    }
  }

  @Nested
  class AliasedSelectionSpecificationTests {
    @Test
    void givenAliasedSelectionSpecification_whenGetAlias_thenReturnAlias() {
      AliasedSelectionSpecification<Object> spec = AliasedSelectionSpecification.of(
          (root, cb) -> root.get("id"),
          "customAlias"
      );

      assertThat(spec.getAlias()).isEqualTo("customAlias");
    }

    @SuppressWarnings("unchecked")
    @Test
    void givenAliasedSelectionSpecification_whenToSelection_thenDelegatesToUnderlyingSpec() {
      Selection<?> mockSelection = mock(Selection.class);
      SelectionSpecification<Object> delegate = (root, cb) -> mockSelection;
      AliasedSelectionSpecification<Object> spec = AliasedSelectionSpecification.of(delegate, "alias");

      Root<Object> root = mock(Root.class);
      CriteriaBuilder cb = mock(CriteriaBuilder.class);

      Selection<?> result = spec.toSelection(root, cb);

      assertThat(result).isSameAs(mockSelection);
    }
  }

  @Nested
  class SelectionSpecificationDefaultMethodTests {
    @Test
    void givenSelectionSpecificationLambda_whenGetAlias_thenReturnNull() {
      SelectionSpecification<Object> spec = (root, cb) -> root.get("id");

      assertThat(spec.getAlias()).isNull();
    }
  }
}
