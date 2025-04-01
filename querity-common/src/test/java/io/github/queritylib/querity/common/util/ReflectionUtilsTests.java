package io.github.queritylib.querity.common.util;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReflectionUtilsTests {

  @Test
  void givenInterfaceImplementedByClassesAndOneAbstractClass_whenFindSubclasses_thenReturnTheImplementingConcreteClasses() {
    assertThat(ReflectionUtils.findSubclasses(MyInterface.class))
        .containsExactlyInAnyOrder(MyClass1.class, MyClass2.class);
  }

  @Test
  void givenClassProperty_whenGetAccessibleField_thenReturnAccessibleField() {
    var obj = new MyClass1();
    assertThat(ReflectionUtils.getAccessibleField(MyClass1.class, "stringValue"))
        .map(field -> field.canAccess(obj))
        .contains(true);
  }

  @Test
  void givenSuperclassProperty_whenGetAccessibleField_thenReturnAccessibleField() {
    var obj = new MyClass1();
    assertThat(ReflectionUtils.getAccessibleField(MyClass1.class, "superclassStringValue"))
        .map(field -> field.canAccess(obj))
        .contains(true);
  }

  @Test
  void givenMultipleClassesAndOnlyOneClassHavingConstructorStringArgument_whenFindClassWithConstructorArgumentOfType_thenReturnTheClassHavingConstructorStringArgument() {
    assertThat(ReflectionUtils.findClassWithConstructorArgumentOfType(
        new HashSet<>(Arrays.asList(MyClass1.class, MyClass2.class)),
        String.class))
        .contains(MyClass1.class);
  }

  @Test
  void givenClassWithConstructorStringArgument_whenConstructInstanceWithArgument_thenReturnAnInstanceOfTheClassWithTheFieldSetToTheConstructorArgumentValue() {
    assertThat(ReflectionUtils.constructInstanceWithArgument(MyClass1.class, "test"))
        .isInstanceOf(MyClass1.class)
        .matches(c -> c.stringValue.equals("test"));
  }

  @Test
  void givenClassWithoutSingleArgumentConstructor_whenConstructInstanceWithArgument_thenThrowsException() {
    assertThrows(NoSuchMethodException.class, () -> ReflectionUtils.constructInstanceWithArgument(MyClass2.class, "test"));
  }

  public interface MyInterface {
  }

  public abstract static class MySuperclass implements MyInterface {
    private String superclassStringValue;
  }

  public static class MyClass1 extends MySuperclass {
    private String stringValue;

    public MyClass1() {
    }

    public MyClass1(String stringValue) {
      this(stringValue, null);
    }

    public MyClass1(String stringValue, String otherStringValue) {
      this.stringValue = stringValue;
    }
  }

  public static class MyClass2 implements MyInterface {
  }
}
