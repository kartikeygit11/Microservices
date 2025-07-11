package com.learningmicroservice.section1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:application.yml")
public class Section1Application {

	public static void main(String[] args) {
		SpringApplication.run(Section1Application.class, args);
	}

}
