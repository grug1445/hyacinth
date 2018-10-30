package com.hyacinth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

/**
 * Created by feichen on 2018/10/29.
 */
@Slf4j
@SpringBootApplication
@Configuration
public class HyacinthServiceContext {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(HyacinthServiceContext.class);
        springApplication.setRegisterShutdownHook(false);
        springApplication.run(args);
        RateLimiterManager rateLimiterManager = new RateLimiterManager();
        rateLimiterManager.bootstrap();
    }
}
