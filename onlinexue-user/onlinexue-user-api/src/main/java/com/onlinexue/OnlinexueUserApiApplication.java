package com.onlinexue;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableSwagger2Doc
@SpringBootApplication
@MapperScan("com.onlinexue.mapper")
public class OnlinexueUserApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnlinexueUserApiApplication.class, args);
    }

}
