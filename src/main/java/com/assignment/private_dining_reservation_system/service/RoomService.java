package com.assignment.private_dining_reservation_system.service;

import com.assignment.private_dining_reservation_system.entity.Restaurant;
import com.assignment.private_dining_reservation_system.entity.Room;
import com.assignment.private_dining_reservation_system.entity.RoomType;
import com.assignment.private_dining_reservation_system.exception.EntityNotFoundException;
import com.assignment.private_dining_reservation_system.model.request.RoomRequest;
import com.assignment.private_dining_reservation_system.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public Room createRoom(Restaurant restaurant, RoomRequest roomRequest) {
        Room room = new Room();
        room.setRoomName(roomRequest.roomName());
        room.setRestaurant(restaurant);
        room.setMinCapacity(roomRequest.minCapacity());
        room.setMaxCapacity(roomRequest.maxCapacity());
        room.setRoomType(roomRequest.roomType());
        room.setMinSpendInCents(roomRequest.minSpendInCents());
        return roomRepository.save(room);
    }

    public Room getByIdAndRestaurantId(Long roomId, Long restaurantId) {
        return roomRepository.findByIdAndRestaurantId(roomId, restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Room not found with room Id: " + roomId));
    }

    public List<Room> getByRoomTypeAndRestaurantId(RoomType roomType, Long restaurantId) {
        return roomRepository.findByRoomTypeAndRestaurantId(roomType, restaurantId);
    }

    public Room updateRoom(Restaurant restaurant, Long roomId, RoomRequest roomRequest) {
        Room room = getByIdAndRestaurantId(roomId, restaurant.getId());
        room.setRoomName(roomRequest.roomName());
        room.setMinCapacity(roomRequest.minCapacity());
        room.setMaxCapacity(roomRequest.maxCapacity());
        room.setRoomType(roomRequest.roomType());
        room.setMinSpendInCents(roomRequest.minSpendInCents());
        return roomRepository.save(room);
    }

    public List<Room> getRoomsForRestaurant(Long restaurantId) {
        return roomRepository.findByRestaurantId(restaurantId);
    }
}
