package io.github.queritylib.querity.spring.web;

import io.github.queritylib.querity.api.Condition;
import io.github.queritylib.querity.api.Querity;
import io.github.queritylib.querity.api.Query;
import io.github.queritylib.querity.api.QueryPreprocessor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Aspect
public class QuerityPreprocessorAspect {

  private final ApplicationContext applicationContext;

  public QuerityPreprocessorAspect(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
  public void anyRestControllerMethod() {
    // no code needed, just the annotation
  }

  @Pointcut("execution(public * *(..))")
  public void anyPublicMethod() {
    // no code needed, just the annotation
  }

  @Pointcut("execution(* *(.., @io.github.queritylib.querity.spring.web.WithPreprocessor (*), ..))")
  public void annotatedArgument() {
    // no code needed, just the annotation
  }

  @Around("anyRestControllerMethod() && anyPublicMethod() && annotatedArgument()")
  public Object applyPreprocessorsToQuerityArgs(ProceedingJoinPoint pjp) throws Throwable {
    Object[] args = pjp.getArgs();
    MethodSignature signature = (MethodSignature) pjp.getSignature();
    Method method = signature.getMethod();
    Parameter[] params = method.getParameters();

    Object[] preprocessedArgs = applyAnyWithPreprocessorAnnotation(args, params);

    return pjp.proceed(preprocessedArgs);
  }

  private Object[] applyAnyWithPreprocessorAnnotation(Object[] args, Parameter[] params) {
    return IntStream
        .range(0, params.length)
        .mapToObj(i -> {
          Object arg = args[i];
          Parameter param = params[i];
          return applyAnyWithPreprocessorAnnotation(arg, param);
        })
        .toArray(Object[]::new);
  }

  private Object applyAnyWithPreprocessorAnnotation(Object arg, Parameter param) {
    return findWithProcessorAnnotation(param)
        .map(annotation -> preprocessArg(arg, annotation))
        .orElse(arg);
  }

  private Optional<WithPreprocessor> findWithProcessorAnnotation(Parameter param) {
    return Optional.ofNullable(AnnotationUtils.findAnnotation(param, WithPreprocessor.class));
  }

  private Object preprocessArg(Object arg, WithPreprocessor annotation) {
    if (arg instanceof Query query) {
      return preprocessQuery(query, annotation);
    } else if (arg instanceof Condition condition) {
      return preprocessCondition(condition, annotation);
    } else return arg;
  }

  private Query preprocessQuery(Query query, WithPreprocessor withPreprocessor) {
    List<QueryPreprocessor> preprocessors = getQueryPreprocessors(withPreprocessor);
    return preprocessQuery(query, preprocessors);
  }

  private Query preprocessQuery(Query query, List<QueryPreprocessor> preprocessors) {
    Query queryWithPreprocessors = query.toBuilder()
        .preprocessors(preprocessors)
        .build();
    return queryWithPreprocessors.preprocess();
  }

  private Condition preprocessCondition(Condition condition, WithPreprocessor withPreprocessor) {
    List<QueryPreprocessor> preprocessors = getQueryPreprocessors(withPreprocessor);
    return preprocessCondition(condition, preprocessors);
  }

  private Condition preprocessCondition(Condition condition, List<QueryPreprocessor> preprocessors) {
    Query query = Querity.wrapConditionInQuery(condition);
    Query preprocessedQuery = preprocessQuery(query, preprocessors);
    return preprocessedQuery.getFilter();
  }

  private List<QueryPreprocessor> getQueryPreprocessors(WithPreprocessor withPreprocessor) {
    return Arrays.stream(withPreprocessor.beanName())
        .map(beanName -> applicationContext.getBean(beanName, QueryPreprocessor.class))
        .toList();
  }
}
