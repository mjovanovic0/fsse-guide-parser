package com.fsse.parser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableJpaRepositories
@SpringBootApplication
@EntityScan("com.fsse.model")
public class FsseGuideParserApplication {

    public static void main(String[] args) {
        SpringApplication.run(FsseGuideParserApplication.class, args);
    }
}
