package io.github.queritylib.querity.spring.web;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface WithPreprocessor {
  @AliasFor("beanName")
  String[] value() default {};

  @AliasFor("value")
  String[] beanName() default {};
}
