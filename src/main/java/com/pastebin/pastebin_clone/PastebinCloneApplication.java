package com.pastebin.pastebin_clone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling; // <--- Import this

@SpringBootApplication
@EnableScheduling // <--- Add this line
public class PastebinCloneApplication {

	public static void main(String[] args) {
		SpringApplication.run(PastebinCloneApplication.class, args);
	}

}