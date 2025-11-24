package com.assignment.private_dining_reservation_system.model.request;

import com.assignment.private_dining_reservation_system.entity.RoomType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RoomRequest(
        @NotBlank
        @Schema(type = "string",
                example = "Test ABC",
                description = "Name of the Room")
        String roomName,

        @Min(1)
        @Schema(type = "number",
                example = "10",
                description = "Min capacity for the room")
        @Max(Integer.MAX_VALUE)
        int minCapacity,

        @Min(1)
        @Schema(type = "number",
                example = "10",
                description = "Max capacity for the room")
        @Max(Integer.MAX_VALUE)
        int maxCapacity,

        @NotNull
        @Schema(type = "string",
                example = "ROOFTOP",
                description = "Room Type",
                allowableValues = {"PRIVATE_ROOM", "HALL", "ROOFTOP"})
        RoomType roomType,

        @NotNull
        @Schema(type = "number",
                example = "10.0",
                description = "min Spend for reservation in restaurant local currency"
        )
        BigDecimal minSpendInCents
) {
}
