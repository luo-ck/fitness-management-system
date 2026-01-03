package com.fitness.system;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestBCrypt {
    
    @Test
    public void testBCryptPassword() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // 测试数据库中存储的哈希值
        String dbHash = "$2a$10$EIXhRjFqBdO0t0RjFqBdO0t0RjFqBdO0t0RjFqBdO0t0RjFqBdO";
        String password = "12345678";
        
        System.out.println("Testing BCrypt hash:");
        System.out.println("Database hash: " + dbHash);
        System.out.println("Password: " + password);
        
        try {
            boolean matches = encoder.matches(password, dbHash);
            System.out.println("Matches: " + matches);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        // 生成一个新的有效哈希值进行比较
        String newHash = encoder.encode(password);
        System.out.println("\nNew valid hash: " + newHash);
        boolean newMatches = encoder.matches(password, newHash);
        System.out.println("New hash matches: " + newMatches);
        
        // 打印新哈希值，用于更新数据库
        System.out.println("\nUse this hash to update the database:");
        System.out.println("UPDATE fitness.admins SET password_hash = '" + newHash + "' WHERE username = 'admin';");
    }
}