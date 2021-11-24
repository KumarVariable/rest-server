package com.rest.services;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

/**
 * Boot Main Class to load application context, required configuration and
 * dependencies.
 * 
 */
@SpringBootApplication
public class StartMyBootAppServer {

	Logger log = LoggerFactory.getLogger(StartMyBootAppServer.class);

	public static void main(String[] args) {
		SpringApplication.run(StartMyBootAppServer.class, args);

	}

	@PostConstruct
	public void init() {
		// log.info("Post Construct Called");

		// Set TimeZone
		// TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	@EventListener(ApplicationReadyEvent.class)
	public void reportCurrentTime() {

		RuntimeMXBean mx = ManagementFactory.getRuntimeMXBean();
		
		AppUtils.startTime = mx.getStartTime();
		AppUtils.appUpTime = mx.getUptime();

		log.info("VM start time : " + new Date(mx.getStartTime()));
		log.info("VM up time : " + mx.getUptime() + " ms");
		
	}

}
