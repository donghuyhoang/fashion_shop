package com.javaweb.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Tắt CSRF vì đang dùng JWT
            .cors(cors -> {}) // Để Spring Security mở cho cấu hình CORS hiện tại (@CrossOrigin)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Mở cửa tự do cho Đăng nhập, Đăng ký
                .requestMatchers("/api/users/login", "/api/users/register").permitAll()
                // Mở cửa tự do cho các API đọc thông tin sản phẩm (GET)
                .requestMatchers(HttpMethod.GET, "/api/products/**", "/api/categories/**", "/api/brands/**", "/api/sizes/**", "/api/colors/**", "/api/product-details/**").permitAll()
                // Phân quyền: Thêm, sửa, xóa sản phẩm chỉ ADMIN (roleId = 1) mới được phép
                .requestMatchers(HttpMethod.POST, "/api/products/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/products/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN")
                // Mở cửa triệt để cho toàn bộ các file giao diện tĩnh bất kể nằm ở thư mục nào
                // Mở cửa triệt để cho toàn bộ các file giao diện tĩnh (Chuẩn Spring Boot 3)
                .requestMatchers(
                    "/", 
                    "/*.html", 
                    "/css/**", 
                    "/js/**", 
                    "/images/**", 
                    "/assets/**", 
                    "/fonts/**", 
                    "/vendor/**",
                    "/uploads/**"
                ).permitAll()
                // Các API nhạy cảm khác yêu cầu phải có Token hợp lệ
                .anyRequest().authenticated()
            )
            // Lắp đặt chốt chặn JWT kiểm tra vé vào trước khi xử lý request
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}