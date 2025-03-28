package io.github.queritylib.querity.spring.data.elasticsearch;

import io.github.queritylib.querity.spring.data.elasticsearch.domain.PersonRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@SpringBootApplication
public class QuerityElasticsearchTestApplication {
  public static void main(String[] args) {
    SpringApplication.run(QuerityElasticsearchTestApplication.class, args);
  }

  @Configuration
  @EnableElasticsearchRepositories(basePackageClasses = PersonRepository.class)
  public static class ClientConfig extends ElasticsearchConfiguration {
    @Value("${spring.elasticsearch.uris}")
    private String hostAndPort;

    @Override
    public ClientConfiguration clientConfiguration() {
      return ClientConfiguration.builder()
          .connectedTo(hostAndPort).build();
    }
  }
}
