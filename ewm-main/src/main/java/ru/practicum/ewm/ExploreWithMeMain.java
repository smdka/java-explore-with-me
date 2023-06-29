package ru.practicum.ewm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@RefreshScope
@SpringBootApplication
public class ExploreWithMeMain {
    public static void main(String[] args) {
        SpringApplication.run(ExploreWithMeMain.class, args);
    }
}
