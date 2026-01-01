package com.chisimdi.product.service;

import com.chisimdi.events.OrderEvent;
import com.chisimdi.product.service.models.Products;
import com.chisimdi.product.service.repositories.ProductsRepository;
import com.chisimdi.product.service.repositories.ReleaseStockIdempotencyRepository;
import com.chisimdi.product.service.repositories.ReserveStockIdempotencyRepository;
import com.chisimdi.product.service.services.EventService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {
    @Mock
    private KafkaTemplate<String,Object> kafkaTemplate;
    @Mock
    private ProductsRepository productsRepository;
    @Mock
    private ReserveStockIdempotencyRepository reserveStockIdempotencyRepository;
    @Mock
    private ReleaseStockIdempotencyRepository releaseStockIdempotencyRepository;
    @InjectMocks
    EventService eventService;

    @Test
    void reserveStockTest(){
        OrderEvent orderEvent=new OrderEvent();
        orderEvent.setProductName("Apple");
        orderEvent.setAmount(2);

        Products products=new Products();
        products.setStockSize(4);
        products.setName("Apple");

        when(productsRepository.findByName(orderEvent.getProductName())).thenReturn(products);

        eventService.reserveStocks(orderEvent);

        assertThat(products.getStockSize()).isEqualTo(2);

        verify(kafkaTemplate).send(eq("inventory-reserved"),any(OrderEvent.class));



    }
    @Test
    void reserveStockTest_StockSizeNotEnough(){
        OrderEvent orderEvent=new OrderEvent();
        orderEvent.setProductName("Apple");
        orderEvent.setAmount(2);

        Products products=new Products();
        products.setStockSize(1);
        products.setName("Apple");

        when(productsRepository.findByName(orderEvent.getProductName())).thenReturn(products);

        eventService.reserveStocks(orderEvent);

        assertThat(products.getStockSize()).isEqualTo(1);

        verify(kafkaTemplate).send(eq("reservation-failed"),any(OrderEvent.class));



    }
    @Test
    void releaseStockTest_StockSizeNotEnough(){
        OrderEvent orderEvent=new OrderEvent();
        orderEvent.setProductName("Apple");
        orderEvent.setAmount(2);

        Products products=new Products();
        products.setStockSize(1);
        products.setName("Apple");

        when(productsRepository.findByName(orderEvent.getProductName())).thenReturn(products);

        eventService.releaseStocks(orderEvent);

        assertThat(products.getStockSize()).isEqualTo(3);

        verify(productsRepository).findByName(orderEvent.getProductName());



    }
}
