package com.assignment.private_dining_reservation_system.service;

import com.assignment.private_dining_reservation_system.event.TableReservedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ReservationEventConsumer {

    @KafkaListener(
            topics = "${spring.kafka.topic}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "concurrentKafkaListenerContainerFactory"
    )
    public void reservationConsumerEvent(TableReservedEvent tableReservedEvent) {
        log.info("Reservation Id: {}, Restaurant Name: {}, Room Name: {}, Reservation date: {}, Diner Email: {}, Reservation Status: {}",
                tableReservedEvent.reservationId(),
                tableReservedEvent.restaurantName(),
                tableReservedEvent.roomName(),
                tableReservedEvent.reservationDate(),
                tableReservedEvent.dinerEmail(),
                tableReservedEvent.reservationStatus());
    }
}
