package com.rest.services.config;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class DemoWebConfig {

	/*
	 * public WebMvcConfigurer corsConfigurer() {
	 * 
	 * return new WebMvcConfigurer() {
	 * 
	 * @Override public void addCorsMappings(CorsRegistry registry) {
	 * registry.addMapping("/greeting-javaconfig").allowedOrigins(
	 * "http://localhost:8000");
	 * 
	 * } };
	 * 
	 * }
	 */

}
