package com.chisimdi.order.service.configurations;

import com.chisimdi.order.service.filters.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    @Autowired
    JwtAuthFilter jwtsAuthFilter;
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity){
        return httpSecurity.csrf(csrf->csrf.disable()).authorizeHttpRequests(auth->auth.requestMatchers("/swagger-ui/**","/v3/**","/swagger-ui.html").permitAll().anyRequest().authenticated()).addFilterBefore(jwtsAuthFilter, UsernamePasswordAuthenticationFilter.class ).build();
    }
}
