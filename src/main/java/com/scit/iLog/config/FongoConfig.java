package com.scit.iLog.config;

import com.github.fakemongo.Fongo;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@Profile("dev") // 'dev' 프로필에서만 활성화
public class FongoConfig {

    @Bean
    public MongoClient mongoClient() {
        Fongo fongo = new Fongo("fongo-db");
        return MongoClients.create(fongo.getServerAddress().toString());
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), "ilog");
    }
}

