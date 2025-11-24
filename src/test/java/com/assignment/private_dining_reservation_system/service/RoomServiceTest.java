package com.assignment.private_dining_reservation_system.service;

import com.assignment.private_dining_reservation_system.BaseTest;
import com.assignment.private_dining_reservation_system.entity.Restaurant;
import com.assignment.private_dining_reservation_system.entity.Room;
import com.assignment.private_dining_reservation_system.entity.RoomType;
import com.assignment.private_dining_reservation_system.exception.EntityNotFoundException;
import com.assignment.private_dining_reservation_system.model.request.RoomRequest;
import com.assignment.private_dining_reservation_system.repository.RoomRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class RoomServiceTest extends BaseTest {

    @Mock
    RoomRepository roomRepository;

    @InjectMocks
    RoomService roomService;

    @Test
    void createRoom() {
        Restaurant restaurant = getRestaurant();
        Room room = getRoom();
        RoomRequest roomRequest = getRoomRequest();
        when(roomRepository.save(any(Room.class))).thenReturn(room);
        Room roomResult =roomService.createRoom(restaurant, roomRequest);
        assertEquals(room.getRestaurant(),roomResult.getRestaurant());
        assertEquals(room.getRoomName(), roomResult.getRoomName());
        assertEquals(room.getMinCapacity(), roomResult.getMinCapacity());
        assertEquals(room.getMaxCapacity(), roomResult.getMaxCapacity());
        assertEquals(room.getRoomType(), roomResult.getRoomType());
    }

    @Test
    void getByIdAndRestaurantId() {
        Room room = getRoom();
        when(roomRepository.findByIdAndRestaurantId(anyLong(),anyLong())).thenReturn(Optional.of(room));
        Room roomResult = roomService.getByIdAndRestaurantId(1L,1L);
        assertEquals(room.getRestaurant(),roomResult.getRestaurant());
        assertEquals(room.getRoomName(), roomResult.getRoomName());
        assertEquals(room.getMinCapacity(), roomResult.getMinCapacity());
        assertEquals(room.getMaxCapacity(), roomResult.getMaxCapacity());
        assertEquals(room.getRoomType(), roomResult.getRoomType());
        verify(roomRepository,times(1)).findByIdAndRestaurantId(anyLong(),anyLong());
    }

    @Test
    void getByIdAndRestaurantId_ExceptionTest() {
        when(roomRepository.findByIdAndRestaurantId(anyLong(),anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> roomService.getByIdAndRestaurantId(1L,1L));
        verify(roomRepository,times(1)).findByIdAndRestaurantId(anyLong(),anyLong());
    }


    @Test
    void getByRoomTypeAndRestaurantId() {
        List<Room> roomList = List.of(getRoom());
        when(roomRepository.findByRoomTypeAndRestaurantId(any(RoomType.class), anyLong())).thenReturn(roomList);
        List<Room> roomListResult = roomService.getByRoomTypeAndRestaurantId(RoomType.ROOFTOP, 1L);
        assertEquals(roomList.size(), roomListResult.size());
        assertEquals(roomList.getFirst().getRoomName(), roomListResult.getFirst().getRoomName());
        assertEquals(roomList.getFirst().getRoomType(), roomListResult.getFirst().getRoomType());
        verify(roomRepository, times(1)).findByRoomTypeAndRestaurantId(any(RoomType.class), anyLong());
    }

    @Test
    void updateRoom() {
        Restaurant restaurant = getRestaurant();
        restaurant.setId(1L);
        Room room = getRoom();
        RoomRequest roomRequest = getRoomRequest();
        when(roomRepository.findByIdAndRestaurantId(anyLong(),anyLong())).thenReturn(Optional.of(room));
        when(roomRepository.save(any(Room.class))).thenReturn(room);
        Room roomResult = roomService.updateRoom(restaurant,1L, roomRequest);
        assertEquals(room.getRestaurant(),roomResult.getRestaurant());
        assertEquals(room.getRoomName(), roomResult.getRoomName());
        assertEquals(room.getMinCapacity(), roomResult.getMinCapacity());
        assertEquals(room.getMaxCapacity(), roomResult.getMaxCapacity());
        assertEquals(room.getRoomType(), roomResult.getRoomType());
    }

    @Test
    void getRoomsForRestaurant() {
        Room room = getRoom();
        List<Room> roomList = List.of(room);
        when(roomRepository.findByRestaurantId(anyLong())).thenReturn(roomList);
        List<Room> roomListResult = roomService.getRoomsForRestaurant(1L);
        assertEquals(roomList.size(), roomListResult.size());
        assertEquals(roomList.getFirst().getRoomName(), roomListResult.getFirst().getRoomName());
        assertEquals(roomList.getFirst().getRoomType(), roomListResult.getFirst().getRoomType());
        verify(roomRepository, times(1)).findByRestaurantId(anyLong());
    }
}