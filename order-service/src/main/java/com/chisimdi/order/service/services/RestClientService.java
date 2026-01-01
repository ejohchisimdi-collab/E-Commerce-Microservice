package com.chisimdi.order.service.services;

import com.chisimdi.order.service.exceptions.ConflictException;
import com.chisimdi.order.service.exceptions.FallBackException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

@Service
public class RestClientService {
    private static final Logger log = LoggerFactory.getLogger(RestClientService.class);
    @Qualifier("productClient")
    private RestClient productClient;
    @Qualifier("accountClient")
    private RestClient accountClient;
    @Qualifier("userClient")
    private RestClient userClient;

    public RestClientService(RestClient productClient,RestClient accountClient,RestClient userClient){
        this.productClient=productClient;
        this.accountClient=accountClient;
        this.userClient=userClient;
    }

    @CircuitBreaker(name = "productService",fallbackMethod = "basePriceFallBack")
    public BigDecimal getProductBasePrice(String name){
        return productClient.get().uri("/products/{name}/price",name)
                .retrieve().body(BigDecimal.class);
    }
    public BigDecimal basePriceFallBack(String name,Throwable t){
       log.error(t.getMessage(),t);
        return BigDecimal.valueOf(0);
    }

    @CircuitBreaker(name = "productService",fallbackMethod = "productExistsFallBack")
    public Boolean doesProductExist(String name){
        return productClient.get().uri("/products/{name}/exists",name)
                .retrieve().body(Boolean.class);
    }
    public Boolean productExistsFallBack(String name,Throwable t){
        log.error(t.getMessage(),t);
        throw new FallBackException("Products are currently not available at the moment");
    }

    @CircuitBreaker(name = "productService",fallbackMethod = "getProductStockFallBack")
    public int  getProductStock(String name){
        return productClient.get().uri("/products/{name}/stocks",name)
                .retrieve().body(Integer.class);
    }
    public int getProductStockFallBack(String name,Throwable t){
        log.error(t.getMessage(),t);
        throw new FallBackException("Products are currently unavailable");
    }




@CircuitBreaker(name = "financeService",fallbackMethod = "doesUserExistFallback")
    public Boolean doesAccountAndUserExist(int accountId, int userId){
        return accountClient.get().uri("/accounts/{accountId}/users/{userId}/exists",accountId,userId)
                .retrieve().body(Boolean.class);
    }

    public Boolean doesUserExistFallback(int accountId,int userId,Throwable t){
        log.error(t.getMessage(),t);
        throw new FallBackException("User accounts are currently not available at the moment");
    }

    @CircuitBreaker(name = "financeService",fallbackMethod = "doesMethodExistFallback")
    public Boolean doesMerchantAccountExist(){
        return accountClient.get().uri("/accounts/merchants/exists").retrieve().body(Boolean.class);
    }

    public Boolean doesMethodExistFallback(Throwable t){
        log.error(t.getMessage(),t);
        throw new FallBackException("Merchant accounts are currently unavailable at the moment");
    }

    @CircuitBreaker(name = "userService",fallbackMethod = "doesUserExistFallBack")
    public Boolean doesUserExist(int id){
        return userClient.get().uri("/users/{id}/exists",id).retrieve().body(Boolean.class);
    }

    public Boolean doesUserExistFallBack(int id,Throwable t){
        log.error(t.getMessage(),t);
        throw new FallBackException("User profiles are currently not available at the moment");
    }


}
