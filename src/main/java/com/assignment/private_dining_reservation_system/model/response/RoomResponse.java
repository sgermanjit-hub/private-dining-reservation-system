package com.assignment.private_dining_reservation_system.model.response;

import com.assignment.private_dining_reservation_system.entity.RoomType;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

public record RoomResponse(
        Long roomId,
        String roomName,
        int minCapacity,
        int maxCapacity,
        Long restaurantId,
        RoomType roomType,
        BigDecimal minSpend,
        LocalTime roomOpeningTime,
        LocalTime roomClosingTime,
        Set<DayOfWeek> openDays
) {
}
