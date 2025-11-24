package com.assignment.private_dining_reservation_system.model.response;

import com.assignment.private_dining_reservation_system.entity.ReservationStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservationResponse(
        Long id,
        Long restaurantId,
        String restaurantName,
        Long roomId,
        String roomName,
        LocalDate reservationDate,
        @Schema(type = "string", example = "18:00") @JsonFormat(pattern = "HH:mm") LocalTime reservationStartTime,
        @Schema(type = "string", example = "18:00") @JsonFormat(pattern = "HH:mm") LocalTime reservationEndTime,
        int groupSize,
        ReservationStatus reservationStatus,
        String dinerEmail
) {
}
