package com.rest.services.model;

import java.util.Date;

public class User {

	private String username;
	private String emailId;
	private Date requestDate;

	private String serviceUptime;

	public User() {
		// default constructor
	}

	public User(String username, String emailId, Date requestDate,
			String serviceUptime) {
		super();
		this.username = username;
		this.emailId = emailId;
		this.requestDate = requestDate;
		this.serviceUptime = serviceUptime;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public Date getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}

	public String getServiceUptime() {
		return serviceUptime;
	}

	public void setServiceUptime(String serviceUptime) {
		this.serviceUptime = serviceUptime;
	}

	@Override
	public String toString() {
		return "User [username=" + username + ", emailId=" + emailId
				+ ", requestDate=" + requestDate + ", serviceUptime="
				+ serviceUptime + "]";
	}

}