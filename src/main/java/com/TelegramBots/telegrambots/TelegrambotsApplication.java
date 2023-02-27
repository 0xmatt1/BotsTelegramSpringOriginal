package com.TelegramBots.telegrambots;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
public class TelegrambotsApplication {

	public static void main(String[] args) {
		SpringApplication.run(TelegrambotsApplication.class, args);
	}

}
