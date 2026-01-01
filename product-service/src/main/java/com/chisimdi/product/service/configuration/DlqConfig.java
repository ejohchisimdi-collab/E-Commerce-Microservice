package com.chisimdi.product.service.configuration;

import com.chisimdi.product.service.exceptions.ResourceNotFoundException;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class DlqConfig {
    @Bean
    public DefaultErrorHandler kafkaErrorHandler(KafkaTemplate<String,Object>kafkaTemplate) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate, (consumerRecord, e) -> new TopicPartition(consumerRecord.topic() + ".DLQ", consumerRecord.partition()));
        FixedBackOff backOff = new FixedBackOff(1000L, 5);
        DefaultErrorHandler defaultErrorHandler = new DefaultErrorHandler(recoverer, backOff);
        defaultErrorHandler.addNotRetryableExceptions(ResourceNotFoundException.class, IllegalArgumentException.class);
        return defaultErrorHandler;
    };

    @Bean
    ConcurrentKafkaListenerContainerFactory<String,Object>kafkaListenerContainerFactory(ConsumerFactory<String,Object>consumerFactory,DefaultErrorHandler defaultErrorHandler){
        ConcurrentKafkaListenerContainerFactory<String,Object>factory=new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(defaultErrorHandler);
        return factory;
    }

}
