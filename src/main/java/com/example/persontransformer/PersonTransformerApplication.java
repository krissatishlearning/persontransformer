package com.example.persontransformer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EntityScan(basePackages = "com.example.persontransformer.audit")
@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.example.persontransformer.repository")
@EnableJpaRepositories(basePackages = "com.example.persontransformer.audit")
public class PersonTransformerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PersonTransformerApplication.class, args);
    }
}
