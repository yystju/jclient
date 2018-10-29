package com.sunlight.client;

import com.sunlight.client.gui.fx.LoginWindow;
import javafx.application.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ClientApplication {
    private static Logger logger = LoggerFactory.getLogger(ClientApplication.class);

    private static ApplicationContext __context;

    public static ApplicationContext getContext() {
        return __context;
    }

    @Autowired
    ApplicationContext context;

    @Bean
    CommandLineRunner commandLineRunner() {
        return (String... strings) -> {
            __context = context;

            Application.launch(LoginWindow.class, strings);
        };
    }

	public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        SpringApplication.run(ClientApplication.class, args);
	}
}
