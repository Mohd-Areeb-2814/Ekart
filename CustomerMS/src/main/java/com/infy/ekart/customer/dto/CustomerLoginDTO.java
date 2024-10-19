package com.infy.ekart.customer.dto;

import javax.validation.constraints.NotNull;

public class CustomerLoginDTO {

	@NotNull(message = "{email.absent}")
	private String emailId;
	
	@NotNull(message = "{password.absent}")
	private String password;

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	
}
