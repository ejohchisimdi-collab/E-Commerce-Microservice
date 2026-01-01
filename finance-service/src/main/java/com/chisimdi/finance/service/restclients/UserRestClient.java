package com.chisimdi.finance.service.restclients;

import com.chisimdi.finance.service.exceptions.FallBackException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.apache.kafka.common.protocol.types.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class UserRestClient {
    private static final Logger log = LoggerFactory.getLogger(UserRestClient.class);
    @Qualifier("userClient")
    private RestClient userClient;

    public UserRestClient(RestClient userClient){
        this.userClient=userClient;
    }

    @CircuitBreaker(name = "userService",fallbackMethod = "doesUserExistFallBack")
    public Boolean doesUserExist(int userId){
        return userClient.get().
                uri("/users/{id}/exists",userId)
                .retrieve().body(Boolean.class);
    }
    public Boolean doesUserExistFallBack(int userId,Throwable t){
        log.error(t.getMessage(),t);
        throw new FallBackException("User profiles not available at the moment");
    }
}
