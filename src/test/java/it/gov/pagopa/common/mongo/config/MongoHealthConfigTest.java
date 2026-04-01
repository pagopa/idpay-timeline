package it.gov.pagopa.common.mongo.config;

import it.gov.pagopa.common.config.CustomMongoHealthIndicator;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class MongoHealthConfigTest {

    @Test
    void shouldCreateCustomMongoHealthIndicator() {
        MongoTemplate mongoTemplate = mock(MongoTemplate.class);

        CustomMongoHealthIndicator healthIndicator = new MongoHealthConfig()
                .customMongoHealthIndicator(mongoTemplate);

        assertThat(healthIndicator).isNotNull();
    }
}
