package com.assignment.private_dining_reservation_system.model.response;

import com.assignment.private_dining_reservation_system.entity.RoomType;

import java.math.BigDecimal;

public record RoomResponse(
        Long roomId,
        String roomName,
        int minCapacity,
        int maxCapacity,
        Long restaurantId,
        RoomType roomType,
        BigDecimal minSpend
) {
}
