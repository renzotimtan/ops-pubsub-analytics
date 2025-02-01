package com.renzotimtan.ops_analytics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OpsAnalyticsApplication {

	public static void main(String[] args) {
		SpringApplication.run(OpsAnalyticsApplication.class, args);
	}

}
