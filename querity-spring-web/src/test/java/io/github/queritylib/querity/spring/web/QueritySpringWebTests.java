package io.github.queritylib.querity.spring.web;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = QueritySpringWebMvcTestController.class)
@Import(QueritySpringWebAutoConfiguration.class)
class QueritySpringWebTests {
  @Autowired
  MockMvc mockMvc;

  /**
   * Tests JSON deserialization and serialization of a Query object given as a REST endpoint query parameter
   */
  @ParameterizedTest
  @ValueSource(strings = {
      /* empty query */               "{}",
      /* single simple condition */   "{\"filter\":{\"propertyName\":\"lastName\",\"operator\":\"EQUALS\",\"value\":\"Skywalker\"}}",
      /* no value condition */        "{\"filter\":{\"propertyName\":\"lastName\",\"operator\":\"IS_NULL\"}}",
      /* and conditions wrapper */    "{\"filter\":{\"and\":[{\"propertyName\":\"lastName\",\"operator\":\"EQUALS\",\"value\":\"Skywalker\"}]}}",
      /* or conditions wrapper */     "{\"filter\":{\"or\":[{\"propertyName\":\"lastName\",\"operator\":\"EQUALS\",\"value\":\"Skywalker\"}]}}",
      /* nested conditions wrapper */ "{\"filter\":{\"and\":[{\"propertyName\":\"lastName\",\"operator\":\"EQUALS\",\"value\":\"Skywalker\"},{\"or\":[{\"propertyName\":\"firstName\",\"operator\":\"EQUALS\",\"value\":\"Anakin\"},{\"propertyName\":\"firstName\",\"operator\":\"EQUALS\",\"value\":\"Luke\"}]}]}}",
      /* pagination */                "{\"pagination\":{\"page\":1,\"pageSize\":20}}",
      /* sort */                      "{\"sort\":[{\"propertyName\":\"lastName\"},{\"propertyName\":\"firstName\",\"direction\":\"DESC\"}]}",
      /* not single condition */      "{\"filter\":{\"not\":{\"propertyName\":\"lastName\",\"operator\":\"EQUALS\",\"value\":\"Skywalker\"}}}",
      /* not conditions wrapper */    "{\"filter\":{\"not\":{\"and\":[{\"propertyName\":\"lastName\",\"operator\":\"EQUALS\",\"value\":\"Skywalker\"}]}}}",
  })
  void givenJsonQuery_whenGetQuery_thenReturnsTheSameQueryAsResponse(String query) throws Exception {
    mockMvc.perform(get("/query")
            .queryParam("q", query))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(query, false));
  }

  @Test
  void givenInvalidJsonQuery_whenGetQuery_thenReturnsBadRequest() throws Exception {
    mockMvc.perform(get("/query")
            .queryParam("q", "{"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void givenNoQuery_whenGetQuery_thenReturnsEmptyResponse() throws Exception {
    mockMvc.perform(get("/query"))
        .andExpect(status().isOk())
        .andExpect(content().string(""));
  }

  @Test
  void givenEmptyStringAsQuery_whenGetQuery_thenReturnsEmptyResponse() throws Exception {
    mockMvc.perform(get("/query")
            .queryParam("q", ""))
        .andExpect(status().isOk())
        .andExpect(content().string(""));
  }

  /**
   * Tests JSON deserialization and serialization of a Condition object given as a REST endpoint query parameter
   */
  @ParameterizedTest
  @ValueSource(strings = {
      /* single simple condition */   "{\"propertyName\":\"lastName\",\"operator\":\"EQUALS\",\"value\":\"Skywalker\"}",
      /* no value condition */        "{\"propertyName\":\"lastName\",\"operator\":\"IS_NULL\"}",
      /* and conditions wrapper */    "{\"and\":[{\"propertyName\":\"lastName\",\"operator\":\"EQUALS\",\"value\":\"Skywalker\"}]}",
      /* or conditions wrapper */     "{\"or\":[{\"propertyName\":\"lastName\",\"operator\":\"EQUALS\",\"value\":\"Skywalker\"}]}",
      /* nested conditions wrapper */ "{\"and\":[{\"propertyName\":\"lastName\",\"operator\":\"EQUALS\",\"value\":\"Skywalker\"},{\"or\":[{\"propertyName\":\"firstName\",\"operator\":\"EQUALS\",\"value\":\"Anakin\"},{\"propertyName\":\"firstName\",\"operator\":\"EQUALS\",\"value\":\"Luke\"}]}]}",
      /* not single condition */      "{\"not\":{\"propertyName\":\"lastName\",\"operator\":\"EQUALS\",\"value\":\"Skywalker\"}}",
      /* not conditions wrapper */    "{\"not\":{\"and\":[{\"propertyName\":\"lastName\",\"operator\":\"EQUALS\",\"value\":\"Skywalker\"}]}}",
  })
  void givenJsonFilter_whenGetCount_thenReturnsTheSameFilterAsResponse(String filter) throws Exception {
    mockMvc.perform(get("/count")
            .queryParam("filter", filter))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(filter, false));
  }

  @Test
  void givenInvalidJsonFilter_whenGetCount_thenReturnsBadRequest() throws Exception {
    mockMvc.perform(get("/count")
            .queryParam("filter", "{"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void givenEmptyJsonFilter_whenGetCount_thenReturnsBadRequest() throws Exception {
    mockMvc.perform(get("/count")
            .queryParam("filter", "{}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void givenNoFilter_whenGetCount_thenReturnsEmptyResponse() throws Exception {
    mockMvc.perform(get("/count"))
        .andExpect(status().isOk())
        .andExpect(content().string(""));
  }

  @Test
  void givenQuery_whenGetQueryWithPreprocessor_thenReturnsPreprocessedQuery() throws Exception {
    mockMvc.perform(get("/query-with-preprocessor")
            .queryParam("q", "{\"filter\":{\"propertyName\":\"prop1\",\"operator\":\"EQUALS\",\"value\":\"test\"}}"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json("{\"filter\":{\"propertyName\":\"prop2\",\"operator\":\"EQUALS\",\"value\":\"test\"}}", false));
  }

  @Test
  void givenQuery_whenGetQueryWithPreprocessorMultiParams_thenReturnsPreprocessedQuery() throws Exception {
    mockMvc.perform(get("/query-with-preprocessor-multi-params")
            .queryParam("someParam1", "test1")
            .queryParam("someParam2", "test2")
            .queryParam("requiredWithPreprocessorAnnotatedString", "shouldBeIgnoredByPreprocessorAspect")
            .queryParam("q", "{\"filter\":{\"propertyName\":\"prop1\",\"operator\":\"EQUALS\",\"value\":\"test\"}}"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json("{\"filter\":{\"propertyName\":\"prop2\",\"operator\":\"EQUALS\",\"value\":\"test\"}}", false));
  }

  @Test
  void givenCondition_whenGetConditionWithPreprocessor_thenReturnsPreprocessedCondition() throws Exception {
    mockMvc.perform(get("/count-with-preprocessor")
            .queryParam("filter", "{\"propertyName\":\"prop1\",\"operator\":\"EQUALS\",\"value\":\"test\"}"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json("{\"propertyName\":\"prop2\",\"operator\":\"EQUALS\",\"value\":\"test\"}", false));
  }
}
