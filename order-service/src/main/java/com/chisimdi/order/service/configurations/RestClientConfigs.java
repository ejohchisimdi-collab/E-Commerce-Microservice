package com.chisimdi.order.service.configurations;

import com.chisimdi.order.service.services.JwtUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfigs {

    @Autowired
    JwtUtilService jwtUtilService;
    @Value("${product.service.url}")
    private String productServiceUrl;
    @Value("${account.service.url}")
    private String accountServiceUrl;
    @Value("${user.service.url}")
    private String userServiceUrl;


    @Bean
    @Qualifier("productClient")
    public RestClient productClient(){
        return RestClient.builder().defaultHeaders(httpHeaders -> httpHeaders.setBearerAuth(jwtUtilService.generateToken("Service",0,"Service"))).baseUrl(productServiceUrl).build();
    }
    @Bean
    @Qualifier("accountClient")
    public RestClient accountClient(){
        return RestClient.builder().defaultHeaders(httpHeaders -> httpHeaders.setBearerAuth(jwtUtilService.generateToken("Service",0,"Service"))).baseUrl(accountServiceUrl).build();
    }
    @Bean
    @Qualifier("userClient")
    public RestClient userClient(){
        return RestClient.builder().defaultHeaders(httpHeaders -> httpHeaders.setBearerAuth(jwtUtilService.generateToken("Service",0,"Service"))).baseUrl(userServiceUrl).build();
    }
}
