package com.ramy.rec;


import com.ramy.rec.config.DAGConfigParser;
import com.ramy.rec.core.datasource.CSVDataSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public DAGConfigParser dagConfigParser(CSVDataSource csvDataSource) {
        return new DAGConfigParser(csvDataSource);
    }
}