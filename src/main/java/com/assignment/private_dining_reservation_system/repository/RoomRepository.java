package com.assignment.private_dining_reservation_system.repository;

import com.assignment.private_dining_reservation_system.entity.Room;
import com.assignment.private_dining_reservation_system.entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByRestaurantId(Long restaurantId);

    Optional<Room> findByIdAndRestaurantId(Long roomId, Long restaurantId);

    List<Room> findByRoomTypeAndRestaurantId(RoomType roomType, Long restaurantId);
}
