package com.chisimdi.user.service.configurations;

import com.chisimdi.user.service.filters.JwtsAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableMethodSecurity(prePostEnabled = true)
@Configuration
public class SecurityConfig {
    @Autowired
    JwtsAuthFilter jwtsAuthFilter;
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity){
        return httpSecurity.csrf(csrf->csrf.disable()).authorizeHttpRequests(auth->auth.requestMatchers("/users/login","/users/","/swagger-ui/**","/v3/**","/swagger-ui.html").permitAll().anyRequest().authenticated()).addFilterBefore(jwtsAuthFilter, UsernamePasswordAuthenticationFilter.class ).build();
    }
}
