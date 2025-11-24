package com.assignment.private_dining_reservation_system.service;

import com.assignment.private_dining_reservation_system.entity.Reservation;
import com.assignment.private_dining_reservation_system.event.TableReservedEvent;
import com.assignment.private_dining_reservation_system.mapper.EventMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ReservationEventProducer {

    private final KafkaTemplate<Long, TableReservedEvent> tableReservedEventKafkaTemplate;
    private final String topic;
    private final EventMapper eventMapper;

    public ReservationEventProducer(KafkaTemplate<Long, TableReservedEvent> tableReservedEventKafkaTemplate,
                                    @Value("${spring.kafka.topic}") String topic, EventMapper eventMapper) {
        this.tableReservedEventKafkaTemplate = tableReservedEventKafkaTemplate;
        this.topic = topic;
        this.eventMapper = eventMapper;
    }

    public void sendReservationEvent(Reservation reservation) {
        TableReservedEvent tableReservedEvent = eventMapper.mapToTableReservedEvent(reservation);
        tableReservedEventKafkaTemplate.send(topic, reservation.getId(), tableReservedEvent);
        log.info("Reservation event sent successfully for reservation id: {}", tableReservedEvent.reservationId());
    }


}
