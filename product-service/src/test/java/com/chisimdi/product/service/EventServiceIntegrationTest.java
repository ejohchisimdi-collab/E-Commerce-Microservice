package com.chisimdi.product.service;

import com.chisimdi.events.OrderEvent;
import com.chisimdi.product.service.models.Products;
import com.chisimdi.product.service.models.ReserveStockIdempotency;
import com.chisimdi.product.service.repositories.ProductsRepository;
import com.chisimdi.product.service.repositories.ReserveStockIdempotencyRepository;
import com.chisimdi.product.service.services.EventService;
import jakarta.persistence.Embedded;
import org.junit.jupiter.api.Test;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@EmbeddedKafka(topics = {"inventory-reserved","reservation-failed"},partitions = 1)
@SpringBootTest
public class EventServiceIntegrationTest {
    @Autowired
    EventService eventService;
    @MockitoBean
    ProductsRepository productsRepository;

    @MockitoBean
    ReserveStockIdempotencyRepository reserveStockIdempotencyRepository;

    BlockingDeque<Object>blockingDeque=new LinkedBlockingDeque<>();

    @KafkaListener(topics = {"inventory-reserved","reservation-failed"})
    void addEvent(OrderEvent orderEvent){
        blockingDeque.add(orderEvent);
    }

    @Test
    void inventoryReservedTest()throws Exception{
        OrderEvent orderEvent=new OrderEvent();
        orderEvent.setProductName("Apple");
        orderEvent.setAmount(2);
        orderEvent.setId("1");

        Products products=new Products();
        products.setStockSize(4);
        products.setName("Apple");

        when(productsRepository.findByName(orderEvent.getProductName())).thenReturn(products);


        eventService.reserveStocks(orderEvent);

        Object object=blockingDeque.poll(5, TimeUnit.SECONDS);
        assert(object instanceof OrderEvent);



    }
    @Test
    void reservationFailedTest()throws Exception{
        OrderEvent orderEvent=new OrderEvent();
        orderEvent.setProductName("Apple");
        orderEvent.setAmount(5);
        orderEvent.setId("1");

        Products products=new Products();
        products.setStockSize(4);
        products.setName("Apple");

        when(productsRepository.findByName(orderEvent.getProductName())).thenReturn(products);


        eventService.reserveStocks(orderEvent);

        Object object=blockingDeque.poll(5, TimeUnit.SECONDS);
        assert(object instanceof OrderEvent);



    }



}
