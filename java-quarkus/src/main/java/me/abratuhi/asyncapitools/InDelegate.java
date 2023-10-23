package me.abratuhi.asyncapitools;

import io.smallrye.reactive.messaging.kafka.Record;
import jakarta.enterprise.context.ApplicationScoped;
import me.abratuhi.asyncapitools.model.*;
import me.abratuhi.asyncapitools.service.*;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
public class InDelegate implements InDelegateI {

  @Channel("out")
  Emitter<Record<TestKey, TestPayload>> emitter;

  @Override
  public void read(Record<TestKey, TestPayload> record) {
    emitter.send(Record.of(record.key(), record.value()));
  }
}
