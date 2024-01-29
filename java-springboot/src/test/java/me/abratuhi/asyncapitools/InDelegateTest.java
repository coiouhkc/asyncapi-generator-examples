package me.abratuhi.asyncapitools;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import me.abratuhi.asyncapitools.model.TestKey;
import me.abratuhi.asyncapitools.model.TestPayload;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Testcontainers
public class InDelegateTest {
  @Container
  static final KafkaContainer kafka =
      new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.3.3"));

  @DynamicPropertySource
  static void overrideProperties(DynamicPropertyRegistry registry) {
    registry.add("app.kafka.bootstrap-address", kafka::getBootstrapServers);
    registry.add("app.kafka.consumer.in.bootstrap-address", kafka::getBootstrapServers);
    registry.add("app.kafka.producer.out.bootstrap-address", kafka::getBootstrapServers);
  }

  @SpyBean private InDelegate inDelegate;

  @Autowired
  @Qualifier("out")
  private KafkaTemplate<TestKey, TestPayload> kafkaTemplate;
  
  @Captor
  private ArgumentCaptor<TestPayload> testPayloadArgumentCaptor;

  @Test
  void produceConsume() {
    kafkaTemplate.sendDefault(TestPayload.builder().prop4("hello").build());
    verify(inDelegate, timeout(5_000).times(1)).consume(testPayloadArgumentCaptor.capture());
    assertThat(testPayloadArgumentCaptor.getValue().getProp4()).isEqualTo("hello");
  }
}
