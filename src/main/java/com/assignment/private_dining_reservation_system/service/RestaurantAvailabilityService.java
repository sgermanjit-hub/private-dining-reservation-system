package com.assignment.private_dining_reservation_system.service;

import com.assignment.private_dining_reservation_system.entity.Room;
import com.assignment.private_dining_reservation_system.entity.RoomType;
import com.assignment.private_dining_reservation_system.exception.ReservationValidationFailureException;
import com.assignment.private_dining_reservation_system.model.request.ReservationTimeFrame;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RestaurantAvailabilityService {

    private final RoomService roomService;
    private final ReservationValidationService reservationValidationService;

    public RestaurantAvailabilityService(RoomService roomService, ReservationValidationService reservationValidationService) {
        this.roomService = roomService;
        this.reservationValidationService = reservationValidationService;
    }

    public List<Room> findAvailableRoomsByRestaurantId(Long restaurantId, ReservationTimeFrame reservationTimeFrame) {
        if (LocalDate.now().isAfter(reservationTimeFrame.date())) {
            throw new ReservationValidationFailureException("Reservation date cannot be in past.");
        }
        List<Room> rooms = roomService.getRoomsForRestaurant(restaurantId);
        return rooms.stream()
                .filter(room -> isRoomAvailableForSlot(room, reservationTimeFrame))
                .toList();
    }

    public List<Room> findAvailableRoomsByRestaurantAndRoomType(Long restaurantId, RoomType roomType, ReservationTimeFrame reservationTimeFrame) {
        if (LocalDate.now().isAfter(reservationTimeFrame.date())) {
            throw new ReservationValidationFailureException("Reservation date cannot be in past.");
        }
        List<Room> rooms = roomService.getByRoomTypeAndRestaurantId(roomType, restaurantId);
        return rooms.stream()
                .filter(room -> isRoomAvailableForSlot(room, reservationTimeFrame))
                .toList();
    }

    private boolean isRoomAvailableForSlot(Room room, ReservationTimeFrame reservationTimeFrame) {
        boolean overlap = reservationValidationService
                .checkOverlap(room, reservationTimeFrame.date(), reservationTimeFrame.startTime(), reservationTimeFrame.endTime());
        return !overlap;
    }
}
