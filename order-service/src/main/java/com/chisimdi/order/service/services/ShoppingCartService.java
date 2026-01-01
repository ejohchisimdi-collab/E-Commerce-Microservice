package com.chisimdi.order.service.services;

import com.chisimdi.order.service.exceptions.ConflictException;
import com.chisimdi.order.service.exceptions.ResourceNotFoundException;
import com.chisimdi.order.service.models.CartStatus;
import com.chisimdi.order.service.models.ShoppingCart;
import com.chisimdi.order.service.models.ShoppingCartDTO;
import com.chisimdi.order.service.models.ShoppingCartIdempotency;
import com.chisimdi.order.service.repositories.ShoppingCartIdempotencyRepository;
import com.chisimdi.order.service.repositories.ShoppingCartRepository;

import jakarta.persistence.OptimisticLockException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ShoppingCartService {
    RestClientService restClientService;
    ShoppingCartRepository shoppingCartRepository;
    ShoppingCartIdempotencyRepository shoppingCartIdempotencyRepository;

    public ShoppingCartService(RestClientService restClientService,ShoppingCartRepository shoppingCartRepository,ShoppingCartIdempotencyRepository shoppingCartIdempotencyRepository){
        this.restClientService=restClientService;
        this.shoppingCartRepository=shoppingCartRepository;
        this.shoppingCartIdempotencyRepository=shoppingCartIdempotencyRepository;
    }

    public ShoppingCartDTO toShoppingCartDTO(ShoppingCart shoppingCart){
        ShoppingCartDTO shoppingCartDTO=new ShoppingCartDTO();
        shoppingCartDTO.setId(shoppingCart.getId());
        shoppingCartDTO.setUserId(shoppingCart.getUserId());
        if(shoppingCart.getProductName()!=null){
            shoppingCartDTO.setProductName(shoppingCart.getProductName());
        }
        if(shoppingCart.getBasePrice()!=null){
            shoppingCartDTO.setBasePrice(shoppingCart.getBasePrice());
        }
        if(shoppingCart.getTotalPrice()!=null){
            shoppingCartDTO.setTotalPrice(shoppingCart.getTotalPrice());
        }
        if(shoppingCart.getCartStatus()!=null){
            shoppingCartDTO.setCartStatus(shoppingCart.getCartStatus());
        }
        shoppingCartDTO.setAmount(shoppingCart.getAmount());
        return shoppingCartDTO;
    }
    public ShoppingCart addToCart(int userId,String productName,int amount){
        if(!restClientService.doesProductExist(productName)){
            throw new ResourceNotFoundException("Product with name "+productName+" not found");
        }
        if(restClientService.getProductStock(productName)<amount){
            throw new ConflictException("Amount is greater than that available for product with name "+productName);
        }
        if(!restClientService.doesUserExist(userId)){
            throw new ResourceNotFoundException("User with id "+userId+" not found");
        }
        BigDecimal basePrice=restClientService.getProductBasePrice(productName);
        ShoppingCart shoppingCart=new ShoppingCart();
        shoppingCart.setCartStatus(CartStatus.PENDING);
        shoppingCart.setBasePrice(basePrice);
        shoppingCart.setTotalPrice(basePrice.multiply(BigDecimal.valueOf(amount)));
        shoppingCart.setAmount(amount);
        shoppingCart.setProductName(productName);
        shoppingCart.setUserId(userId);
        shoppingCartRepository.save(shoppingCart);
         return shoppingCart;
    }

    @Retryable(retryFor = OptimisticLockException.class,backoff = @Backoff(delay = 200,multiplier = 2),maxAttempts = 5)
    public ShoppingCartDTO addToCartWithIdempotency(String idempotencyKey,int userId,String productName,int amount){
       ShoppingCartIdempotency shoppingCartIdempotency1=shoppingCartIdempotencyRepository.findByIdempotencyKey(idempotencyKey);
       if(shoppingCartIdempotency1!=null){
           return toShoppingCartDTO(shoppingCartIdempotency1.getShoppingCart());
       }

        ShoppingCart shoppingCart= addToCart(userId, productName, amount);
        ShoppingCartIdempotency shoppingCartIdempotency=new ShoppingCartIdempotency();
        shoppingCartIdempotency.setIdempotencyKey(idempotencyKey);
        shoppingCartIdempotency.setShoppingCart(shoppingCart);
        shoppingCartIdempotencyRepository.save(shoppingCartIdempotency);
        return toShoppingCartDTO(shoppingCart);
    }

    public List<ShoppingCartDTO> findAllShoppingCarts(int pageNumber, int size){
        Page<ShoppingCart> shoppingCart=shoppingCartRepository.findAll(PageRequest.of(pageNumber, size));
        List<ShoppingCartDTO>shoppingCartDTOS=new ArrayList<>();
        for(ShoppingCart s: shoppingCart){
            shoppingCartDTOS.add(toShoppingCartDTO(s));
        }
        return shoppingCartDTOS;
    }

    public List<ShoppingCartDTO>findShoppingCartByUserId(int userId,int pageNumber,int size){
        Page<ShoppingCart>shoppingCarts=shoppingCartRepository.findByUserId(userId,PageRequest.of(pageNumber,size));
        List<ShoppingCartDTO>shoppingCartDTOS=new ArrayList<>();
        for(ShoppingCart s: shoppingCarts){
            shoppingCartDTOS.add(toShoppingCartDTO(s));
        }
        return shoppingCartDTOS;
    }
}
