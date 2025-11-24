package com.assignment.private_dining_reservation_system.mapper;

import com.assignment.private_dining_reservation_system.BaseTest;
import com.assignment.private_dining_reservation_system.entity.Reservation;
import com.assignment.private_dining_reservation_system.model.response.ReservationResponse;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ReservationMapperTest extends BaseTest {
    @InjectMocks
    ReservationMapper reservationMapper;

    @Test
    void toResponseTest() {
        Reservation reservation = getReservation();
        ReservationResponse result = reservationMapper.toResponse(reservation);
        assertNotNull(result);
        assertEquals(reservation.getReservationStatus(), result.reservationStatus());
        assertEquals(reservation.getReservationDate(), result.reservationDate());
        assertEquals(reservation.getReservationStartTime(), result.reservationStartTime());
        assertEquals(reservation.getReservationEndTime(), result.reservationEndTime());
        assertEquals(reservation.getDinerEmail(), result.dinerEmail());

    }
}