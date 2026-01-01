package com.chisimdi.product.service.configuration;

import com.chisimdi.product.service.filters.JwtsAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    @Autowired
    JwtsAuthFilter jwtsAuthFilter;
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity){
        return httpSecurity.csrf(csrf->csrf.disable()).authorizeHttpRequests(auth->auth.requestMatchers(HttpMethod.GET,"/products/").permitAll().requestMatchers("/swagger-ui/**","/v3/**","/swagger-ui.html").permitAll().requestMatchers(HttpMethod.GET,"/products/**").permitAll().anyRequest().authenticated()).addFilterBefore(jwtsAuthFilter, UsernamePasswordAuthenticationFilter.class ).build();
    }
}
