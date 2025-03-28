package io.github.queritylib.querity.spring.web;

import io.github.queritylib.querity.api.Condition;
import io.github.queritylib.querity.api.Query;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QueritySpringWebMvcTestController {
  @GetMapping(value = "/query", produces = MediaType.APPLICATION_JSON_VALUE)
  public Query getQuery(@RequestParam(required = false) Query q) {
    return q;
  }

  @GetMapping(value = "/query-with-preprocessor", produces = MediaType.APPLICATION_JSON_VALUE)
  public Query getQueryWithPreprocessor(@RequestParam(required = false) @WithPreprocessor("preprocessor1") Query q) {
    return q;
  }

  @GetMapping(value = "/query-with-preprocessor-multi-params", produces = MediaType.APPLICATION_JSON_VALUE)
  public Query getQueryWithPreprocessorMultipleParams(@RequestParam String someParam1,
                                                      @RequestParam(required = false) @WithPreprocessor("preprocessor1") Query q,
                                                      @RequestParam String someParam2,
                                                      @RequestParam(required = false) @WithPreprocessor("preprocessor1") String notRequiredWithPreprocessorAnnotatedString,
                                                      @RequestParam @WithPreprocessor("preprocessor1") String requiredWithPreprocessorAnnotatedString
  ) {
    return q;
  }

  @GetMapping(value = "/count", produces = MediaType.APPLICATION_JSON_VALUE)
  public Condition getCount(@RequestParam(required = false) Condition filter) {
    return filter;
  }

  @GetMapping(value = "/count-with-preprocessor", produces = MediaType.APPLICATION_JSON_VALUE)
  public Condition getCountWithPreprocessor(@RequestParam(required = false) @WithPreprocessor("preprocessor1") Condition filter) {
    return filter;
  }
}
