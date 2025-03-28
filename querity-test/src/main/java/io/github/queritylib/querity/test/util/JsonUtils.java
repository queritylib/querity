package io.github.queritylib.querity.test.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonUtils {
  @SuppressWarnings("unchecked")
  public static <T> List<T> readListFromJson(String path, Class<T> entityClass) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper().registerModules(new JavaTimeModule());
    Class<T[]> entityArrayClass = (Class<T[]>) Array.newInstance(entityClass, 0).getClass();
    return Arrays.asList(objectMapper.readValue(new ClassPathResource(path).getInputStream(), entityArrayClass));
  }
}
