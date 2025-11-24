package com.assignment.private_dining_reservation_system.config;

import com.assignment.private_dining_reservation_system.event.TableReservedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Long, TableReservedEvent> concurrentKafkaListenerContainerFactory(ConsumerFactory<Long, TableReservedEvent> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<Long, TableReservedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }
}
