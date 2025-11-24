package com.assignment.private_dining_reservation_system.event;

import java.time.LocalDate;
import java.time.LocalTime;

public record TableReservedEvent(
        Long reservationId,
        Long restaurantId,
        String restaurantName,
        Long roomId,
        String roomName,
        LocalDate reservationDate,
        LocalTime reservationStartTime,
        LocalTime reservationEndTime,
        int groupSize,
        String reservationStatus,
        String dinerEmail) {
}
