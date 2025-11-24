package com.assignment.private_dining_reservation_system.model.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservationTimeFrame(
        LocalDate date,
        @Schema(type = "string", example = "18:00") LocalTime startTime,
        @Schema(type = "string", example = "18:00") LocalTime endTime
) {
}
