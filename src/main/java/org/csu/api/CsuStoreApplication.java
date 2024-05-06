package org.csu.api;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class })
@MapperScan("org.csu.api.persistence")
public class CsuStoreApplication {

    public static void main(String[] args) {
        String version = SpringBootVersion.getVersion();
        System.out.println(version);
        SpringApplication.run(CsuStoreApplication.class, args);
    }

}
