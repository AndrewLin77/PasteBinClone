package com.pastebin.pastebin_clone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // Enables the background cleanup job
public class PastebinCloneApplication {

    public static void main(String[] args) {
        SpringApplication.run(PastebinCloneApplication.class, args);
    }

}