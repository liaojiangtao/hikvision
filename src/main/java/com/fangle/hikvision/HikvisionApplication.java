package com.fangle.hikvision;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@ImportAutoConfiguration(value = {EhomeConfig.class})
public class HikvisionApplication {

    public static void main(String[] args) {
        SpringApplication.run(HikvisionApplication.class, args);
    }

}
