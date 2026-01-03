package com.fitness.system.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    /**
     * 生成JWT Token
     * @param username 用户名
     * @param role 角色
     * @return Token
     */
    public String generateToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret.getBytes())
                .compact();
    }

    /**
     * 从Token中获取用户名
     * @param token Token
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        try {
            return Jwts.parser().setSigningKey(jwtSecret.getBytes()).parseClaimsJws(token).getBody().getSubject();
        } catch (Exception e) {
            // Token无效，返回null
            return null;
        }
    }

    /**
     * 从Token中获取角色
     * @param token Token
     * @return 角色
     */
    public String getRoleFromToken(String token) {
        try {
            Claims claims = Jwts.parser().setSigningKey(jwtSecret.getBytes()).parseClaimsJws(token).getBody();
            return (String) claims.get("role");
        } catch (Exception e) {
            // Token无效，返回null
            return null;
        }
    }

    /**
     * 验证Token
     * @param token Token
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret.getBytes()).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            // Token无效
            return false;
        }
    }
}