package com.rest.services.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.rest.services.AppUtils;
import com.rest.services.model.Greeting;
import com.rest.services.model.User;

@RestController
public class ClientRequestController {

	@Autowired
	ResourceLoader resourceLoader;

	Logger log = LoggerFactory.getLogger(ClientRequestController.class);

	private static final String TEMPLATE = "Hai, %s!";
	private final AtomicLong counter = new AtomicLong();

	private static final String DEFAULT_PDF_NAME = "boot_tutorial.pdf";
	private static final String DEFAULT_JPEG_NAME = "sample.jpg";

	@GetMapping(value = {"/", "/show"})
	public Greeting showLandingPage() {
		return new Greeting(counter.incrementAndGet(), TEMPLATE.concat("----"));
	}

	@GetMapping("/currentTime")
	public String currentTime() {

		String timeInCurrentTimeZone = getTimeByTimeZone(TimeZone.getDefault());

		long currentTime = ZonedDateTime.now().toInstant().toEpochMilli();

		long serverStartTime = AppUtils.startTime;

		long runningSince = currentTime - serverStartTime;

		long diffInseconds = TimeUnit.MILLISECONDS.toSeconds(runningSince);

		String response = String.format(
				"Current time : %s .Running since %s seconds",
				timeInCurrentTimeZone, diffInseconds);

		log.info("Returned response uptime() <--> " + response);
		return response;
	}

	@GetMapping("/serviceUptime")
	public String serviceUptime() {

		long uptime = ManagementFactory.getRuntimeMXBean().getUptime();

		String serviceUptime = String.format(
				"%02d hours %02d minutes %02d seconds",
				TimeUnit.MILLISECONDS.toHours(uptime),
				TimeUnit.MILLISECONDS.toMinutes(uptime) - TimeUnit.HOURS
						.toMinutes(TimeUnit.MILLISECONDS.toHours(uptime)),
				TimeUnit.MILLISECONDS.toSeconds(uptime) - TimeUnit.MINUTES
						.toSeconds(TimeUnit.MILLISECONDS.toMinutes(uptime)));

		String response = String.format("Service is running since : %s.",
				serviceUptime);

		log.info("Returned response serviceUptime() <--> " + response);
		return response;
	}

	/**
	 * Enable cross-origin resource sharing only for this specific method. Allow
	 * only http://localhost:12345 to send cross-origin requests.
	 */
	@GetMapping("/greeting")
	@CrossOrigin(origins = "http://localhost:9000")
	public Greeting greeting(
			@RequestParam(required = false, defaultValue = "World") String name) {

		System.out.println("==== get greeting ====");

		return new Greeting(counter.incrementAndGet(),
				String.format(TEMPLATE, name));
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(method = RequestMethod.GET, value = "/greeting-javaconfig", produces = MediaType.APPLICATION_JSON_VALUE)
	public Greeting greetingWithJavaconfig(
			@RequestParam(required = false, defaultValue = "World") String name) {
		System.out.println("==== in greetingWithJavaconfig ====");
		return new Greeting(counter.incrementAndGet(),
				String.format(TEMPLATE, name));
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(method = RequestMethod.POST, value = "/postCall", produces = MediaType.APPLICATION_JSON_VALUE)
	public Greeting postCall() {
		System.out.println("==== inside post call ====");
		return new Greeting(134, "post call");
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(method = RequestMethod.POST, value = "/subscribeUser", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<User> subscribeUser(@RequestBody User user) {
		System.out.println("==== in subscribeUser ====");

		User subscribedUser = new User();

		subscribedUser.setUsername(user.getUsername());
		subscribedUser.setEmailId(user.getEmailId());
		subscribedUser.setRequestDate(new Date());
		subscribedUser.setServiceUptime(serviceUptime());

		return new ResponseEntity<User>(subscribedUser, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/showText", produces = MediaType.TEXT_PLAIN_VALUE)
	public String showText() {
		return "Hell Text User";
	}

	@CrossOrigin(origins = "http://localhost:8000")
	@RequestMapping(method = RequestMethod.GET, value = "/showJson", produces = MediaType.APPLICATION_JSON_VALUE)
	public Greeting showJson() {
		System.out.println("==== in showJson ====");
		return new Greeting(counter.incrementAndGet(), "Hello JSON");
	}

	@GetMapping(value = "/showXml", produces = MediaType.APPLICATION_XML_VALUE)
	@ResponseBody
	public Greeting showXml() {
		return new Greeting(counter.incrementAndGet(), "Hello XML");
	}

	@GetMapping(value = "/showJpeg", produces = MediaType.IMAGE_JPEG_VALUE)
	@ResponseBody
	public byte[] showJpeg() throws IOException {
		Resource resource = resourceLoader
				.getResource("classpath:data/".concat(DEFAULT_JPEG_NAME));

		InputStream in = resource.getInputStream();

		return IOUtils.toByteArray(in);

	}

	@GetMapping(value = "/downloadPdf", produces = MediaType.APPLICATION_PDF_VALUE)
	@ResponseBody
	public void downloadPdf(HttpServletResponse response) {
		Resource resource = resourceLoader
				.getResource("classpath:data/".concat(DEFAULT_PDF_NAME));
		try {
			File file = resource.getFile();

			boolean fileExists = file.exists();

			System.out.println("File Exists :: " + fileExists);

			response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
					"attachment;filename=" + file.getName());

			response.setContentLength((int) file.length());

			BufferedInputStream inStream = new BufferedInputStream(
					new FileInputStream(file));
			BufferedOutputStream outStream = new BufferedOutputStream(
					response.getOutputStream());

			byte[] buffer = new byte[1024];
			int bytesRead = 0;
			while ((bytesRead = inStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, bytesRead);
			}
			outStream.flush();
			inStream.close();

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	/**
	 * private helper method to get Current Time in AM/PM
	 * 
	 * @param timeZone
	 * @return current time based on time zone.
	 */
	private String getTimeByTimeZone(TimeZone timeZone) {

		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat("hh:mm a");

		df.setTimeZone(TimeZone.getTimeZone(timeZone.toZoneId()));

		return df.format(date);
	}

}
