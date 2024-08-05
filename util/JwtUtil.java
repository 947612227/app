package com.app.user.util;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {
    // 使用固定的密钥生成器，避免每次应用启动时生成不同的密钥
    private static final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    private final long expirationTime = 86400000; // 1 day in milliseconds

    // 生成 token
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey)
                .compact();
    }


    // 验证 token 的有效性
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // 可以记录异常日志以供调试
            e.printStackTrace();
            return false;
        }
    }

    // 从 token 中获取用户名
    public String getUsernameFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            // 可以记录异常日志以供调试
            e.printStackTrace();
            return null;
        }
    }

    // 其他方法...
}
