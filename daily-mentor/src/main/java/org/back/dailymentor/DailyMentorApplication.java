package org.back.dailymentor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DailyMentorApplication {

  public static void main(String[] args) {
    SpringApplication.run(DailyMentorApplication.class, args);
  }
}
