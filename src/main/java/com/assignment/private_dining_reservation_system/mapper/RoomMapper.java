package com.assignment.private_dining_reservation_system.mapper;

import com.assignment.private_dining_reservation_system.entity.Room;
import com.assignment.private_dining_reservation_system.model.response.RoomResponse;
import org.springframework.stereotype.Component;

@Component
public class RoomMapper {

    public RoomResponse toResponse(Room room) {
        return new RoomResponse(
                room.getId(),
                room.getRoomName(),
                room.getMinCapacity(),
                room.getMaxCapacity(),
                room.getRestaurant().getId(),
                room.getRoomType(),
                room.getMinSpendInCents()
        );
    }
}
