package com.infy.ekart.cart;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class CustomerCartConfig {

	@Bean
	// Make this as load-balanced rest template
	@LoadBalanced   //this will return to me load balanced rest template
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
