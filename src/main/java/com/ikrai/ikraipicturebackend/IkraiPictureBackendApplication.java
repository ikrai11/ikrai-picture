package com.ikrai.ikraipicturebackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@MapperScan("com.ikrai.ikraipicturebackend.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
@EnableAsync
public class IkraiPictureBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(IkraiPictureBackendApplication.class, args);
    }

}
