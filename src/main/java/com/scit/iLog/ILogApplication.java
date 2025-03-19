package com.scit.iLog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@ServletComponentScan
@EnableFeignClients
@EnableMongoAuditing
@EnableJpaAuditing
@SpringBootApplication
public class ILogApplication {

    public static void main(String[] args) {
        SpringApplication.run(ILogApplication.class, args);
    }

}
