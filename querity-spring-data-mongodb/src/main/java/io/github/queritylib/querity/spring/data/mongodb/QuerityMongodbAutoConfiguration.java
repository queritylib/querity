package io.github.queritylib.querity.spring.data.mongodb;

import io.github.queritylib.querity.api.Querity;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;

@AutoConfiguration
@ConditionalOnProperty(prefix = "querity.autoconfigure", name = "enabled", matchIfMissing = true)
public class QuerityMongodbAutoConfiguration {
  @Bean
  public Querity querity(MongoTemplate mongoTemplate) {
    return new QuerityMongodbImpl(mongoTemplate);
  }
}
