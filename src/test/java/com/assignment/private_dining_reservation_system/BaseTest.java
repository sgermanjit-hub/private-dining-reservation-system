package com.assignment.private_dining_reservation_system;

import com.assignment.private_dining_reservation_system.entity.*;
import com.assignment.private_dining_reservation_system.model.request.AutoAssignReservationRequest;
import com.assignment.private_dining_reservation_system.model.request.ReservationRequest;
import com.assignment.private_dining_reservation_system.model.request.RestaurantRequest;
import com.assignment.private_dining_reservation_system.model.request.RoomRequest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@ExtendWith(MockitoExtension.class)
public class BaseTest {
    protected Restaurant getRestaurant() {
        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantName("restaurant");
        restaurant.setAddress("address");
        restaurant.setContact("+1xxxxxx");
        restaurant.setEmail("res@gmail.com");
        restaurant.setLocalCurrencyCode(RestaurantCurrency.AUD);
        return restaurant;
    }

    protected RestaurantRequest getRestaurantRequest() {
        return new RestaurantRequest("restaurant", "address", "+1xxxxxx", "res@gmail.com", RestaurantCurrency.AUD);
    }

    protected RoomRequest getRoomRequest() {
        return new RoomRequest("room", 10, 20, RoomType.ROOFTOP, new BigDecimal(500));
    }

    protected Room getRoom() {
        Room room = new Room();
        room.setRestaurant(getRestaurant());
        room.setRoomType(RoomType.ROOFTOP);
        room.setMinSpendInCents(new BigDecimal(500));
        room.setMinCapacity(10);
        room.setMaxCapacity(15);
        room.setRoomName("room");
        return room;
    }

    protected ReservationRequest getReservationRequest() {
        return new ReservationRequest(1L, 1L, LocalTime.now(), LocalTime.now().plusHours(4), LocalDate.now(), 10, "diner@email.com");
    }

    protected AutoAssignReservationRequest getAutoAssignReservationRequest() {
        return new AutoAssignReservationRequest(1L, RoomType.ROOFTOP, LocalTime.now(), LocalTime.now().plusHours(4), LocalDate.now(), 10, "diner@email.com");
    }

    protected Reservation getReservation() {
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setReservationStatus(ReservationStatus.CONFIRMED);
        reservation.setRoom(getRoom());
        reservation.setReservationDate(LocalDate.now().plusDays(2));
        reservation.setDinerEmail("diner@gmail.com");
        reservation.setReservationStartTime(LocalTime.now());
        reservation.setReservationEndTime(LocalTime.now().plusHours(4));
        reservation.setRestaurant(getRestaurant());
        reservation.setGroupSize(10);
        return reservation;
    }

    protected RoomCalendar getRoomCalendar() {
        RoomCalendar roomCalendar = new RoomCalendar();
        roomCalendar.setRoom(getRoom());
        roomCalendar.setReservationDate(LocalDate.now());
        return roomCalendar;
    }
}
