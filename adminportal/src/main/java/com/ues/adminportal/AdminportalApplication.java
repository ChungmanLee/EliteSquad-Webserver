package com.ues.adminportal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.ues.adminportal.service", "com.ues.adminportal.repository", "com.ues.adminportal.entity","com.ues.adminportal.controller"})
public class AdminportalApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdminportalApplication.class, args);
	}

}
