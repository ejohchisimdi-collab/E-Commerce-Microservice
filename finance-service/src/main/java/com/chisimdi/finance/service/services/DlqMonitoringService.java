package com.chisimdi.finance.service.services;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class DlqMonitoringService {

    private static final Logger log = LoggerFactory.getLogger(DlqMonitoringService.class);

    @KafkaListener(topics = "inventory-reserved.DLQ")
    public void consumeDlQ(ConsumerRecord<String,Object>record){
    log.info("Dlq message received");
    log.info("Topic: {}",record.topic());
    log.info("Partition: {}",record.partition());
    log.info("Offset: {}",record.offset());
    log.info("Delivery count: {}",record.deliveryCount());
    log.info("Timestamp {}",record.timestamp());

    }
}
