package com.assignment.private_dining_reservation_system.model.request;

import com.assignment.private_dining_reservation_system.entity.RoomType;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Email;

import java.time.LocalDate;
import java.time.LocalTime;

public record AutoAssignReservationRequest(

        @NotNull
        @Schema(type = "number",
                example = "1",
                description = "Restaurant unique id")
        Long restaurantId,

        @NotNull
        @Schema(type = "string",
                example = "ROOFTOP",
                description = "Room Type",
                allowableValues = {"PRIVATE_ROOM", "HALL", "ROOFTOP"})
        RoomType roomType,

        @NotNull
        @Schema(type = "string",
                example = "18:00",
                description = "Reservation Start Time")
        @JsonFormat(pattern = "HH:mm")
        LocalTime reservationStartTime,

        @NotNull
        @Schema(type = "string",
                example = "21:00",
                description = "Reservation End Time")
        @JsonFormat(pattern = "HH:mm")
        LocalTime reservationEndTime,

        @NotNull
        @Schema(type = "string",
                example = "2025-11-22",
                description = "Reservation Date")
        @FutureOrPresent(message = "Reservation date must be current or future date.")
        LocalDate reservationDate,

        @Min(1)
        @Schema(type = "number",
                example = "10",
                description = "Group Size for the reservation")
        int groupSize,

        @NotBlank
        @Schema(type = "string",
                example = "abc@diner.com",
                description = "Guest's Email for the reservation")
        @Email
        String dinerEmail
) {
}
