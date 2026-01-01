package com.chisimdi.product.service.services;

import com.chisimdi.events.*;
import com.chisimdi.product.service.models.Products;
import com.chisimdi.product.service.models.ReleaseStocksIdempotency;
import com.chisimdi.product.service.models.ReserveStockIdempotency;
import com.chisimdi.product.service.repositories.ProductsRepository;
import com.chisimdi.product.service.repositories.ReleaseStockIdempotencyRepository;
import com.chisimdi.product.service.repositories.ReserveStockIdempotencyRepository;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EventService {
    private static final Logger log = LoggerFactory.getLogger(EventService.class);
    private KafkaTemplate<String,Object>kafkaTemplate;
    private ProductsRepository productsRepository;
    private ReserveStockIdempotencyRepository reserveStockIdempotencyRepository;
    private ReleaseStockIdempotencyRepository releaseStockIdempotencyRepository;

    public EventService(KafkaTemplate<String,Object> kafkaTemplate,ProductsRepository productsRepository,ReserveStockIdempotencyRepository reserveStockIdempotencyRepository, ReleaseStockIdempotencyRepository releaseStockIdempotencyRepository){
        this.kafkaTemplate=kafkaTemplate;
        this.productsRepository=productsRepository;
        this.releaseStockIdempotencyRepository=releaseStockIdempotencyRepository;
        this.reserveStockIdempotencyRepository=reserveStockIdempotencyRepository;
    }

@Retryable(retryFor = OptimisticLockException.class,maxAttempts = 5,backoff = @Backoff(delay = 200,multiplier = 3.0))
    @Transactional
    @KafkaListener(topics = "order-created")
    public void reserveStocks(OrderEvent orderEvent){

        ReserveStockIdempotency reserveStockIdempotency1=reserveStockIdempotencyRepository.findById(orderEvent.getId()).orElse(null);
        if(reserveStockIdempotency1!=null){
            return;
        }

        Products products=productsRepository.findByName(orderEvent.getProductName());
        if(products.getStockSize()>=orderEvent.getAmount()){
            products.setStockSize(products.getStockSize()-orderEvent.getAmount());
            productsRepository.save(products);
            kafkaTemplate.send("inventory-reserved",orderEvent);

        }
        else {
            kafkaTemplate.send("reservation-failed",orderEvent);
        }
        ReserveStockIdempotency reserveStockIdempotency=new ReserveStockIdempotency();
        reserveStockIdempotency.setId(orderEvent.getId());
        reserveStockIdempotency.setLocalDateTime(LocalDateTime.now());
        reserveStockIdempotencyRepository.save(reserveStockIdempotency);
    }

    @Retryable(retryFor = OptimisticLockException.class,maxAttempts = 5,backoff = @Backoff(delay = 200,multiplier = 3.0))
    @Transactional
    @KafkaListener(topics = "payment-failed")
    public void releaseStocks(OrderEvent orderEvent){
        ReleaseStocksIdempotency releaseStocksIdempotency1=releaseStockIdempotencyRepository.findById(orderEvent.getId()).orElse(null);
        if(releaseStocksIdempotency1!=null){
            return;
        }
        Products products=productsRepository.findByName(orderEvent.getProductName());
        products.setStockSize(products.getStockSize()+orderEvent.getAmount());
        productsRepository.save(products);
        ReleaseStocksIdempotency releaseStocksIdempotency=new ReleaseStocksIdempotency();
        releaseStocksIdempotency.setId(orderEvent.getId());
        releaseStocksIdempotency.setLocalDateTime(LocalDateTime.now());
        releaseStockIdempotencyRepository.save(releaseStocksIdempotency);

    }

}
