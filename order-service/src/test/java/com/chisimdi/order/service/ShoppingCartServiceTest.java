package com.chisimdi.order.service;

import com.chisimdi.order.service.exceptions.ConflictException;
import com.chisimdi.order.service.exceptions.ResourceNotFoundException;
import com.chisimdi.order.service.models.CartStatus;
import com.chisimdi.order.service.models.ShoppingCart;
import com.chisimdi.order.service.repositories.ShoppingCartIdempotencyRepository;
import com.chisimdi.order.service.repositories.ShoppingCartRepository;
import com.chisimdi.order.service.services.RestClientService;
import com.chisimdi.order.service.services.ShoppingCartService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShoppingCartServiceTest {
    @Mock
    RestClientService restClientService;
    @Mock
    ShoppingCartRepository shoppingCartRepository;
    @Mock
    ShoppingCartIdempotencyRepository shoppingCartIdempotencyRepository;
    @InjectMocks
    ShoppingCartService shoppingCartService;

    @Test
    void addToCartTest(){
        int userId=1;
        String productName="Apple";
        int amount=4;
        ShoppingCart shoppingCart=new ShoppingCart();
        ArgumentCaptor<ShoppingCart>captor=ArgumentCaptor.forClass(ShoppingCart.class);

        when(restClientService.doesProductExist(productName)).thenReturn(true);
        when(restClientService.getProductStock(productName)).thenReturn(5);
        when(restClientService.doesUserExist(userId)).thenReturn(true);
        when(restClientService.getProductBasePrice(productName)).thenReturn(BigDecimal.ONE);
        when(shoppingCartRepository.save(any(ShoppingCart.class))).thenReturn(captor.capture());


        shoppingCartService.addToCart(userId,productName,amount);

        verify(shoppingCartRepository).save(captor.capture());
        shoppingCart=captor.getValue();

        assertThat(shoppingCart.getCartStatus()).isEqualTo(CartStatus.PENDING);
        assertThat(shoppingCart.getTotalPrice()).isEqualTo(BigDecimal.valueOf(4));
        assertThat(shoppingCart.getBasePrice()).isEqualTo(BigDecimal.valueOf(1));

        verify(restClientService).getProductStock(productName);
        verify(restClientService).getProductStock(productName);
        verify(restClientService).doesUserExist(userId);
        verify(shoppingCartRepository).save(any(ShoppingCart.class));


    }
    @Test
    void addToCartTest_ThrowsResourceNotFoundExceptionForProduct(){
        int userId=1;
        String productName="Apple";
        int amount=4;
        ShoppingCart shoppingCart=new ShoppingCart();
        ArgumentCaptor<ShoppingCart>captor=ArgumentCaptor.forClass(ShoppingCart.class);

        when(restClientService.doesProductExist(productName)).thenReturn(false);


       assertThatThrownBy(()-> shoppingCartService.addToCart(userId,productName,amount)).isInstanceOf(ResourceNotFoundException.class);




        verify(restClientService,never()).getProductStock(productName);
        verify(restClientService,never()).getProductStock(productName);
        verify(restClientService,never()).doesUserExist(userId);
        verify(shoppingCartRepository,never()).save(any(ShoppingCart.class));


    }
    @Test
    void addToCartTest_ThrowsConflictException(){
        int userId=1;
        String productName="Apple";
        int amount=4;
        ShoppingCart shoppingCart=new ShoppingCart();
        ArgumentCaptor<ShoppingCart>captor=ArgumentCaptor.forClass(ShoppingCart.class);

        when(restClientService.doesProductExist(productName)).thenReturn(true);
        when(restClientService.getProductStock(productName)).thenReturn(3);


        assertThatThrownBy(()->shoppingCartService.addToCart(userId,productName,amount)).isInstanceOf(ConflictException.class);


        verify(restClientService).getProductStock(productName);
        verify(restClientService,never()).doesUserExist(userId);
        verify(shoppingCartRepository,never()).save(any(ShoppingCart.class));


    }
    @Test
    void addToCartTest_ThrowsResourceNOtFoundExceptionForUser(){
        int userId=1;
        String productName="Apple";
        int amount=4;
        ShoppingCart shoppingCart=new ShoppingCart();
        ArgumentCaptor<ShoppingCart>captor=ArgumentCaptor.forClass(ShoppingCart.class);

        when(restClientService.doesProductExist(productName)).thenReturn(true);
        when(restClientService.getProductStock(productName)).thenReturn(5);
        when(restClientService.doesUserExist(userId)).thenReturn(false);


        assertThatThrownBy(()->shoppingCartService.addToCart(userId,productName,amount)).isInstanceOf(ResourceNotFoundException.class);




        verify(restClientService).getProductStock(productName);
        verify(restClientService).getProductStock(productName);
        verify(restClientService).doesUserExist(userId);
        verify(shoppingCartRepository,never()).save(any(ShoppingCart.class));


    }


}
