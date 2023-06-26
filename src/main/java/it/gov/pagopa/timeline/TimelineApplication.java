package it.gov.pagopa.timeline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "it.gov.pagopa")
public class TimelineApplication {

  public static void main(String[] args) {
    SpringApplication.run(TimelineApplication.class, args);
  }

}
