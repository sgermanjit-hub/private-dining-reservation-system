package com.assignment.private_dining_reservation_system.mapper;

import com.assignment.private_dining_reservation_system.entity.Reservation;
import com.assignment.private_dining_reservation_system.model.response.ReservationResponse;
import org.springframework.stereotype.Component;

@Component
public class ReservationMapper {
    public ReservationResponse toResponse(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getRestaurant().getId(),
                reservation.getRestaurant().getRestaurantName(),
                reservation.getRoom().getId(),
                reservation.getRoom().getRoomName(),
                reservation.getReservationDate(),
                reservation.getReservationStartTime(),
                reservation.getReservationEndTime(),
                reservation.getGroupSize(),
                reservation.getReservationStatus(),
                reservation.getDinerEmail()
        );
    }
}
