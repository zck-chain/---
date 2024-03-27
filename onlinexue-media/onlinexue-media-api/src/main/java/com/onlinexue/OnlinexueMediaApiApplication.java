package com.onlinexue;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.onlinexue.mapper")
public class OnlinexueMediaApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnlinexueMediaApiApplication.class, args);
    }

}
