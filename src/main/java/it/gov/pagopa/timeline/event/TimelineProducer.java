package it.gov.pagopa.timeline.event;

import it.gov.pagopa.timeline.dto.PutOperationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
public class TimelineProducer {

  @Value("${kafka.topic.timeline}")
  private String timelineTopic;
  @Autowired
  StreamBridge streamBridge;

  public void sendOperation(PutOperationDTO putOperationDTO){
    streamBridge.send(timelineTopic, putOperationDTO);
  }

}
