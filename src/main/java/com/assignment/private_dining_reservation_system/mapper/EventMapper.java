package com.assignment.private_dining_reservation_system.mapper;

import com.assignment.private_dining_reservation_system.entity.Reservation;
import com.assignment.private_dining_reservation_system.event.TableReservedEvent;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {
    public TableReservedEvent mapToTableReservedEvent(Reservation reservation) {
        return new TableReservedEvent(
                reservation.getId(),
                reservation.getRestaurant().getId(),
                reservation.getRestaurant().getRestaurantName(),
                reservation.getRoom().getId(),
                reservation.getRoom().getRoomName(),
                reservation.getReservationDate(),
                reservation.getReservationStartTime(),
                reservation.getReservationEndTime(),
                reservation.getGroupSize(),
                reservation.getReservationStatus().name(),
                reservation.getDinerEmail()
        );

    }
}
