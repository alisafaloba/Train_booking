package com.alisafaloba.trainbooking.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Keep this disabled for Postman testing
                .headers(headers -> headers.frameOptions(frame -> frame.disable())) // For H2 console access
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll()
                        // Lock down Admin endpoints to ONLY the Admin role
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // All other endpoints can be accessed by anyone who is logged in
                        .anyRequest().authenticated()
                )
                .httpBasic(withDefaults());

        return http.build();
    }

    // We must expose the PasswordEncoder as a Bean so Spring Security
    // knows how to check the passwords during login
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}