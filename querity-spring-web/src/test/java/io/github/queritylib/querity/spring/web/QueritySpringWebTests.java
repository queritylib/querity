package io.github.queritylib.querity.spring.web;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.json.JsonCompareMode;
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
      /* in array condition */        "{\"filter\":{\"propertyName\":\"lastName\",\"operator\":\"IN\",\"value\":[\"Skywalker\",\"Solo\"]}}",
      // Note: Tests with 'select' have been removed as Query no longer supports select.
      // Use AdvancedQuery for projections (select, groupBy, having).
  })
  void givenJsonQuery_whenGetQuery_thenReturnsTheSameQueryAsResponse(String query) throws Exception {
    mockMvc.perform(get("/query")
            .queryParam("q", query))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(query, JsonCompareMode.LENIENT));
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
      /* in array condition */        "{\"propertyName\":\"lastName\",\"operator\":\"IN\",\"value\":[\"Skywalker\",\"Solo\"]}",
  })
  void givenJsonFilter_whenGetCount_thenReturnsTheSameFilterAsResponse(String filter) throws Exception {
    mockMvc.perform(get("/count")
            .queryParam("filter", filter))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(filter, JsonCompareMode.LENIENT));
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
        .andExpect(content().json("{\"filter\":{\"propertyName\":\"prop2\",\"operator\":\"EQUALS\",\"value\":\"test\"}}", JsonCompareMode.LENIENT));
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
        .andExpect(content().json("{\"filter\":{\"propertyName\":\"prop2\",\"operator\":\"EQUALS\",\"value\":\"test\"}}", JsonCompareMode.LENIENT));
  }

  @Test
  void givenCondition_whenGetConditionWithPreprocessor_thenReturnsPreprocessedCondition() throws Exception {
    mockMvc.perform(get("/count-with-preprocessor")
            .queryParam("filter", "{\"propertyName\":\"prop1\",\"operator\":\"EQUALS\",\"value\":\"test\"}"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json("{\"propertyName\":\"prop2\",\"operator\":\"EQUALS\",\"value\":\"test\"}", JsonCompareMode.LENIENT));
  }

  /**
   * Tests JSON deserialization and serialization of an AdvancedQuery object given as a REST endpoint query parameter
   */
  @ParameterizedTest
  @ValueSource(strings = {
      /* empty advanced query */
      "{}",
      /* select with property names */
      "{\"select\":{\"propertyNames\":[\"firstName\",\"lastName\"]}}",
      /* select with expressions - property references */
      "{\"select\":{\"expressions\":[{\"propertyName\":\"firstName\"},{\"propertyName\":\"lastName\",\"alias\":\"surname\"}]}}",
      /* select with function expression */
      "{\"select\":{\"expressions\":[{\"function\":\"UPPER\",\"arguments\":[{\"propertyName\":\"lastName\"}],\"alias\":\"upperName\"}]}}",
      /* select with aggregate function */
      "{\"select\":{\"expressions\":[{\"function\":\"COUNT\",\"arguments\":[{\"propertyName\":\"id\"}],\"alias\":\"total\"}]}}",
      /* group by with property names */
      "{\"select\":{\"expressions\":[{\"propertyName\":\"category\"},{\"function\":\"COUNT\",\"arguments\":[{\"propertyName\":\"id\"}],\"alias\":\"count\"}]},\"groupBy\":{\"propertyNames\":[\"category\"]}}",
      /* group by with expressions */
      "{\"select\":{\"expressions\":[{\"function\":\"UPPER\",\"arguments\":[{\"propertyName\":\"category\"}],\"alias\":\"upperCat\"},{\"function\":\"COUNT\",\"arguments\":[{\"propertyName\":\"id\"}],\"alias\":\"count\"}]},\"groupBy\":{\"expressions\":[{\"function\":\"UPPER\",\"arguments\":[{\"propertyName\":\"category\"}]}]}}",
      /* group by with having - using leftExpression */
      "{\"select\":{\"expressions\":[{\"propertyName\":\"category\"},{\"function\":\"SUM\",\"arguments\":[{\"propertyName\":\"amount\"}],\"alias\":\"total\"}]},\"groupBy\":{\"propertyNames\":[\"category\"]},\"having\":{\"leftExpression\":{\"function\":\"SUM\",\"arguments\":[{\"propertyName\":\"amount\"}]},\"operator\":\"GREATER_THAN\",\"value\":1000}}",
      /* advanced query with filter */
      "{\"filter\":{\"propertyName\":\"status\",\"operator\":\"EQUALS\",\"value\":\"ACTIVE\"},\"select\":{\"propertyNames\":[\"name\",\"status\"]}}",
      /* advanced query with pagination */
      "{\"select\":{\"propertyNames\":[\"name\"]},\"pagination\":{\"page\":1,\"pageSize\":20}}",
      /* advanced query with sort */
      "{\"select\":{\"propertyNames\":[\"name\"]},\"sort\":[{\"propertyName\":\"name\",\"direction\":\"ASC\"}]}",
      /* advanced query with distinct */
      "{\"select\":{\"propertyNames\":[\"category\"]},\"distinct\":true}",
      /* complex advanced query */
      "{\"filter\":{\"propertyName\":\"status\",\"operator\":\"EQUALS\",\"value\":\"COMPLETED\"},\"select\":{\"expressions\":[{\"propertyName\":\"category\"},{\"function\":\"COUNT\",\"arguments\":[{\"propertyName\":\"id\"}],\"alias\":\"orderCount\"},{\"function\":\"SUM\",\"arguments\":[{\"propertyName\":\"amount\"}],\"alias\":\"totalAmount\"}]},\"groupBy\":{\"propertyNames\":[\"category\"]},\"having\":{\"leftExpression\":{\"function\":\"COUNT\",\"arguments\":[{\"propertyName\":\"id\"}]},\"operator\":\"GREATER_THAN\",\"value\":5},\"sort\":[{\"propertyName\":\"totalAmount\",\"direction\":\"DESC\"}],\"pagination\":{\"page\":1,\"pageSize\":10}}"
  })
  void givenJsonAdvancedQuery_whenGetAdvancedQuery_thenReturnsTheSameQueryAsResponse(String query) throws Exception {
    mockMvc.perform(get("/advanced-query")
            .queryParam("q", query))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(query, JsonCompareMode.LENIENT));
  }

  @Test
  void givenInvalidJsonAdvancedQuery_whenGetAdvancedQuery_thenReturnsBadRequest() throws Exception {
    mockMvc.perform(get("/advanced-query")
            .queryParam("q", "{"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void givenNoAdvancedQuery_whenGetAdvancedQuery_thenReturnsEmptyResponse() throws Exception {
    mockMvc.perform(get("/advanced-query"))
        .andExpect(status().isOk())
        .andExpect(content().string(""));
  }

  @Test
  void givenEmptyStringAsAdvancedQuery_whenGetAdvancedQuery_thenReturnsEmptyResponse() throws Exception {
    mockMvc.perform(get("/advanced-query")
            .queryParam("q", ""))
        .andExpect(status().isOk())
        .andExpect(content().string(""));
  }
}
