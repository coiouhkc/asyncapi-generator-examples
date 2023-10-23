package me.abratuhi.asyncapitools;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.kafka.InjectKafkaCompanion;
import io.quarkus.test.kafka.KafkaCompanionResource;
import io.smallrye.reactive.messaging.kafka.Record;
import io.smallrye.reactive.messaging.kafka.companion.ConsumerTask;
import io.smallrye.reactive.messaging.kafka.companion.KafkaCompanion;
import me.abratuhi.asyncapitools.model.*;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTestResource(KafkaCompanionResource.class)
@QuarkusTest
public class InDelegateTest {

  @InjectKafkaCompanion KafkaCompanion companion;

  @BeforeEach
  void setUpKafkaCompanionSerde() {
    companion.registerSerde(TestKey.class, new TestKeySerializer(), new TestKeyDeserializer());
    companion.registerSerde(
        TestPayload.class, new TestPayloadSerializer(), new TestPayloadDeserializer());
  }

  @AfterEach
  void clearTopics() {
    companion.topics().clear("in", "out");
  }

  @Test
  void dup1() {
    TestKey testKey = TestKey.builder().key("key1").build();

    companion
        .produce(TestKey.class, TestPayload.class)
        .fromRecords(List.of(new ProducerRecord<>("in", testKey, TestPayload.builder().build())));

    ConsumerTask<TestKey, TestPayload> task =
        companion.consume(TestKey.class, TestPayload.class).fromTopics("out", 1);

    task.awaitCompletion();

    assertThat(task.count()).isEqualTo(1);
    assertThat(task.getFirstRecord().key().getKey()).isEqualTo("key1");
  }
}
