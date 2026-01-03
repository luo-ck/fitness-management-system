package com.fitness.system;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptTest {
    
    @Test
    public void generateBCryptPassword() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String plainPassword = "12345678";
        String encodedPassword = passwordEncoder.encode(plainPassword);
        System.out.println("Plain Password: " + plainPassword);
        System.out.println("Encoded Password: " + encodedPassword);
    }
}