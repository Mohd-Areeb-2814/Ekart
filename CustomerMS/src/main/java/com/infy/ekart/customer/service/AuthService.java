package com.infy.ekart.customer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

//    @Autowired
//    private CustomerRepository repository;
//    @Autowired
//    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

//    public String saveUser(UserCredential credential) {
//        credential.setPassword(passwordEncoder.encode(credential.getPassword()));
//        repository.save(credential);
//        return "user added to the system";
//    }

    public String generateToken(String username) {
        return jwtService.generateToken(username);
    }

    public void validateToken(String token) {
        jwtService.validateToken(token);
    }


}
