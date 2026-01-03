package com.fitness.system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.fitness.system")
@MapperScan("com.fitness.system.mapper")
public class FitnessSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(FitnessSystemApplication.class, args);
    }
}