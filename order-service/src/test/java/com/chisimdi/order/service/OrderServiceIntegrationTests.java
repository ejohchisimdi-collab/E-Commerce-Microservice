package com.chisimdi.order.service;

import com.chisimdi.events.Location;
import com.chisimdi.events.OrderEvent;
import com.chisimdi.order.service.models.CartStatus;
import com.chisimdi.order.service.models.Orders;
import com.chisimdi.order.service.models.ShoppingCart;
import com.chisimdi.order.service.repositories.ShoppingCartRepository;
import com.chisimdi.order.service.services.OrderService;
import com.chisimdi.order.service.services.RestClientService;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
@EmbeddedKafka(topics = {"order-created"},partitions = 1)
public class OrderServiceIntegrationTests {


    @Autowired
    OrderService orderService;

    @MockitoBean
    RestClientService restClientService;
    @MockitoBean
    ShoppingCartRepository shoppingCartRepository;

    BlockingDeque<Object>blockingDeque= new LinkedBlockingDeque<>();

    @KafkaListener(topics = {"order-created"})
    void addEvents(OrderEvent orderEvent){
        blockingDeque.add(orderEvent);
    }

    @Test
    void orderCreatedTest()throws Exception{
        int cartId=1;
        int userId=2;
        int accountId=3;
        Location location=Location.Albania;

        when(shoppingCartRepository.findByIdAndCartStatus(cartId, CartStatus.PENDING)).thenReturn(Optional.of(new ShoppingCart()));
        when(restClientService.doesAccountAndUserExist(accountId,userId)).thenReturn(true);

        Orders orders=orderService.placeAnOrder(cartId,userId,accountId,location);

        Object object=blockingDeque.poll(5, TimeUnit.SECONDS);
        assert(object instanceof OrderEvent);

    }
}
