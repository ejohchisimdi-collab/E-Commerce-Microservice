package com.chisimdi.product.service.services;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class DlqMonitoringService {

    private static final Logger log = LoggerFactory.getLogger(DlqMonitoringService.class);

    @KafkaListener(topics = "order-created.DLQ")
    public void consumeDlQForOrderCreated(ConsumerRecord<String,Object>record){
        log.info("Dlq message received");
        log.info("Topic: {}",record.topic());
        log.info("Partition: {}",record.partition());
        log.info("Offset: {}",record.offset());
        log.info("Delivery count: {}",record.deliveryCount());
        log.info("Timestamp {}",record.timestamp());

    }

    @KafkaListener(topics = "payment-failed.DLQ")
    public void consumeDlQForFailedPayments(ConsumerRecord<String,Object>record){
        log.info("Dlq message received");
        log.info("Topic: {}",record.topic());
        log.info("Partition: {}",record.partition());
        log.info("Offset: {}",record.offset());
        log.info("Delivery count: {}",record.deliveryCount());
        log.info("Timestamp {}",record.timestamp());

    }
}
