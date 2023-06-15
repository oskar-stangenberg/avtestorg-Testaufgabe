package de.avtest.testaufgabe.juniortask;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("de.avtest.testaufgabe.juniortask.repository")
@EntityScan("de.avtest.testaufgabe.juniortask.data.dbo")
public class JuniorTaskApplication {

  public static void main(String[] args) {
    SpringApplication.run(JuniorTaskApplication.class, args);
  }

}
