package com.infy.ekart.customer.dto;

public class AuthRequest {

	private String emailId;
    private String password;
    
    
	public AuthRequest() {
		super();
	}


	public AuthRequest(String emailId, String password) {
		super();
		this.emailId = emailId;
		this.password = password;
	}


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
