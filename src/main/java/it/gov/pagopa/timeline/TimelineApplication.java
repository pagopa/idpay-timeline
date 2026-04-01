package it.gov.pagopa.timeline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportRuntimeHints;

import it.gov.pagopa.timeline.configuration.NativeRuntimeHints;

@SpringBootApplication(scanBasePackages = "it.gov.pagopa")
@ImportRuntimeHints(NativeRuntimeHints.class)
public class TimelineApplication {

  public static void main(String[] args) {
    SpringApplication.run(TimelineApplication.class, args);
  }

}
