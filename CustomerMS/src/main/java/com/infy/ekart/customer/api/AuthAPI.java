package com.infy.ekart.customer.api;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.infy.ekart.customer.dto.AuthRequest;
import com.infy.ekart.customer.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthAPI {
    @Autowired
    private AuthService service;

    @Autowired
    private AuthenticationManager authenticationManager;

//    @PostMapping("/register")
//    public String addNewUser(@RequestBody UserCredential user) {
//        return service.saveUser(user);
//    }

    @PostMapping("/token")
    public String getToken(@RequestBody AuthRequest authRequest) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmailId(), authRequest.getPassword()));
        if (authenticate.isAuthenticated()) {
            return service.generateToken(authRequest.getEmailId());
        } else {
            throw new RuntimeException("invalid access");
        }
    }

    @GetMapping("/validate")
    public String validateToken(@RequestParam("token") String token) {
    	System.out.println("Inside Validate Token Method");
        service.validateToken(token);
        return "Token is valid";
    }
}
