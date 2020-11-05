package com.example.mobileappws;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.SpringServletContainerInitializer;

import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

@Slf4j
@SpringBootApplication
public class MobileAppWsApplication extends SpringBootServletInitializer {
	private static final Logger LOGGER = Logger.getLogger(MobileAppWsApplication.class.getName());
	private static FileHandler fh;

	static {
		System.out.println("SOUT INTO MAIN CLASS INTO INIT-STATIC SNIPPET");
		LOGGER.info("LOG INTO MAIN CLASS INTO INIT-STATIC SNIPPET");
	}

	// from SpringBootServletInitializer (for creating .war file)
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return super.configure(builder);
	}

	public static void main(String[] args) throws IOException {
		LOGGER.info("BEFORE SPRINGAPPLICATION.run");
		SpringApplication.run(MobileAppWsApplication.class, args);
		LOGGER.info("AFTER SPRINGAPPLICATION.run");
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SpringApplicationContext springApplicationContext() {
		return new SpringApplicationContext();
	}
}
