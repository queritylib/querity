package io.github.queritylib.querity.spring.data.elasticsearch;

import io.github.queritylib.querity.api.Querity;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

@AutoConfiguration
public class QuerityElasticsearchAutoConfiguration {
  @Bean
  public Querity querity(ElasticsearchOperations elasticsearchOperations) {
    return new QuerityElasticsearchImpl(elasticsearchOperations);
  }
}
