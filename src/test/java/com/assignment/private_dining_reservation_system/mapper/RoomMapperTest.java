package com.assignment.private_dining_reservation_system.mapper;

import com.assignment.private_dining_reservation_system.BaseTest;
import com.assignment.private_dining_reservation_system.entity.Room;
import com.assignment.private_dining_reservation_system.model.response.RoomResponse;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RoomMapperTest extends BaseTest {
    @InjectMocks
    RoomMapper roomMapper;

    @Test
    void toResponseTest() {
        Room room = getRoom();
        RoomResponse roomResponse = roomMapper.toResponse(room);
        assertNotNull(roomResponse);
        assertEquals(room.getId(), roomResponse.roomId());
        assertEquals(room.getRoomType(), roomResponse.roomType());
        assertEquals(room.getRoomName(), roomResponse.roomName());
        assertEquals(room.getRestaurant().getId(), roomResponse.restaurantId());
        assertEquals(room.getMinCapacity(), roomResponse.minCapacity());
        assertEquals(room.getMaxCapacity(), roomResponse.maxCapacity());
        assertEquals(room.getMinSpendInCents(), roomResponse.minSpend());
    }
}