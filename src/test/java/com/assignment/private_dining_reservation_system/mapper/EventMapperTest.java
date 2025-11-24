package com.assignment.private_dining_reservation_system.mapper;

import com.assignment.private_dining_reservation_system.BaseTest;
import com.assignment.private_dining_reservation_system.entity.Reservation;
import com.assignment.private_dining_reservation_system.event.TableReservedEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EventMapperTest extends BaseTest {

    @Test
    void mapToTableReservedEvent() {
        Reservation reservation = getReservation();
        TableReservedEvent tableReservedEvent = new EventMapper().mapToTableReservedEvent(reservation);
        assertNotNull(tableReservedEvent);
        assertEquals(reservation.getRoom().getRoomName(), tableReservedEvent.roomName());
        assertEquals(reservation.getRestaurant().getRestaurantName(), tableReservedEvent.restaurantName());
        assertEquals(reservation.getId(), tableReservedEvent.reservationId());
        assertEquals(reservation.getDinerEmail(), tableReservedEvent.dinerEmail());
        assertEquals(reservation.getReservationDate(), tableReservedEvent.reservationDate());
        assertEquals(reservation.getReservationStartTime(), tableReservedEvent.reservationStartTime());
        assertEquals(reservation.getReservationEndTime(), tableReservedEvent.reservationEndTime());
        assertEquals(reservation.getReservationStatus().name(), tableReservedEvent.reservationStatus());
    }
}