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
            .csrf(csrf -> csrf.disable()) 
            .cors(cors -> {}) 
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/users/login", "/api/users/register").permitAll()
                
                .requestMatchers(HttpMethod.GET, 
                    "/api/products", "/api/products/**", 
                    "/api/categories", "/api/categories/**", 
                    "/api/brands", "/api/brands/**", 
                    "/api/sizes", "/api/sizes/**", 
                    "/api/colors", "/api/colors/**", 
                    "/api/product-details", "/api/product-details/**", 
                    "/api/product-images", "/api/product-images/**").permitAll()
                
                .requestMatchers(HttpMethod.POST, "/api/products/**", "/api/product-details/**", "/api/product-images/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/products/**", "/api/product-details/**", "/api/product-images/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/products/**", "/api/product-details/**", "/api/product-images/**").hasRole("ADMIN")
                
                .requestMatchers(
                    "/", "/*.html", "/css/**", "/js/**", "/images/**", "/assets/**", "/fonts/**", "/vendor/**", "/uploads/**", "/error"
                ).permitAll()
                
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}