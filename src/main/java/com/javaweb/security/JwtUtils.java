package com.javaweb.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtils {

    // Khóa bí mật (Ít nhất 256-bit). Ở dự án thực tế, bạn nên lấy từ application.properties
    private static final String SECRET_KEY = "SneakPeakSuperSecretKeyForJwtAuthentication";
    private static final long EXPIRATION_TIME = 86400000; // 1 ngày (mili-giây)

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    // Xưởng đúc Vòng tay: Tạo chuỗi JWT mang theo email, role, userId
    public String generateToken(String email, Integer roleId, Integer userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roleId", roleId);
        claims.put("userId", userId);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractEmail(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody().getSubject();
    }

    // Lấy Role từ vòng tay để biết là Admin hay Khách hàng
    public Integer extractRoleId(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
        return (Integer) claims.get("roleId");
    }

    // Kiểm tra Vòng tay: Hợp lệ, chưa hết hạn, đúng chữ ký?
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}