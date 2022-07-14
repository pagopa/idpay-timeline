package it.gov.pagopa.timeline.event;

import it.gov.pagopa.timeline.dto.QueueOperationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
public class TimelineProducer {

  @Value("${spring.cloud.stream.bindings.timelineQueue-out-0.binder}")
  private String binder;

  @Autowired
  StreamBridge streamBridge;

  public void sendOperation(QueueOperationDTO queueOperationDTO){
    streamBridge.send("timelineQueue-out-0", binder, queueOperationDTO);
  }

}
