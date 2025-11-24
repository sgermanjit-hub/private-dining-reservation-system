package com.assignment.private_dining_reservation_system.config;

import com.assignment.private_dining_reservation_system.event.TableReservedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public KafkaTemplate<Long, TableReservedEvent> tableReservedEventKafkaTemplate(ProducerFactory<Long, TableReservedEvent> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}
