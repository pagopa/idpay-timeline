package it.gov.pagopa.timeline.event.producer;

import it.gov.pagopa.timeline.dto.QueueOperationDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
public class TimelineProducer {

  @Value("${spring.cloud.stream.bindings.consumerTimeline-out-0.binder}")
  private String binder;

  private final StreamBridge streamBridge;

  public TimelineProducer(StreamBridge streamBridge) {
    this.streamBridge = streamBridge;
  }

  public void sendOperation(QueueOperationDTO queueOperationDTO){
    streamBridge.send("consumerTimeline-out-0", binder, queueOperationDTO);
  }

}
