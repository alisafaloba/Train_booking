package com.alisafaloba.trainbooking.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. Disable CSRF so we can send POST requests from Postman
                .csrf(csrf -> csrf.disable())

                // 2. Allow the H2 console to load in the browser (disables frame-busting)
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))

                // 3. Configure access rules
                .authorizeHttpRequests(auth -> auth
                        // Let anyone access the H2 console without logging in
                        .requestMatchers("/h2-console/**").permitAll()

                        // All other API requests require Basic Authentication
                        .anyRequest().authenticated()
                )

                // 4. Enable HTTP Basic Authentication for Postman
                .httpBasic(withDefaults());

        return http.build();
    }
}