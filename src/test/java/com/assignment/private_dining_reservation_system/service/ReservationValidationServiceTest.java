package com.assignment.private_dining_reservation_system.service;

import com.assignment.private_dining_reservation_system.BaseTest;
import com.assignment.private_dining_reservation_system.entity.Reservation;
import com.assignment.private_dining_reservation_system.entity.ReservationStatus;
import com.assignment.private_dining_reservation_system.entity.Room;
import com.assignment.private_dining_reservation_system.exception.ReservationValidationFailureException;
import com.assignment.private_dining_reservation_system.model.request.ReservationRequest;
import com.assignment.private_dining_reservation_system.repository.ReservationRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ReservationValidationServiceTest extends BaseTest {
    @Mock
    ReservationRepository reservationRepository;
    @InjectMocks
    ReservationValidationService reservationValidationService;

    @Test
    void checkOverlap() {
        Room room = getRoom();
        Reservation reservation = getReservation();
        List<Reservation> reservationList = List.of(reservation);
        when(reservationRepository.findByRoomReservationDateAndStatus(any(Room.class), any(LocalDate.class), eq(ReservationStatus.CONFIRMED))).thenReturn(reservationList);
        boolean result = reservationValidationService.checkOverlap(room, LocalDate.now().plusDays(2), LocalTime.now(), LocalTime.now().plusHours(3));
        assertTrue(result);
        verify(reservationRepository, times(1)).findByRoomReservationDateAndStatus(any(Room.class), any(LocalDate.class), eq(ReservationStatus.CONFIRMED));
    }

    @Test
    void checkOverlap_NoOverlapResult() {
        Room room = getRoom();
        when(reservationRepository.findByRoomReservationDateAndStatus(any(Room.class), any(LocalDate.class), eq(ReservationStatus.CONFIRMED))).thenReturn(Collections.emptyList());
        boolean result = reservationValidationService.checkOverlap(room, LocalDate.now(), LocalTime.now(), LocalTime.now().plusHours(3));
        assertFalse(result);
        verify(reservationRepository, times(1)).findByRoomReservationDateAndStatus(any(Room.class), any(LocalDate.class), eq(ReservationStatus.CONFIRMED));
    }

    @Test
    void validateDateAndTime_PastDate() {
        ReservationRequest reservationRequest = new ReservationRequest(
                1L, 1L, LocalTime.now(), LocalTime.now().plusHours(4), LocalDate.now().minusDays(1), 10, "diner@gmail.com"
        );
        assertThrows(ReservationValidationFailureException.class, () -> reservationValidationService.validateDateAndTime(reservationRequest));
    }

    @Test
    void validateDateAndTime_MinBookingHours() {
        ReservationRequest reservationRequest = new ReservationRequest(
                1L, 1L, LocalTime.now(), LocalTime.now().plusHours(2), LocalDate.now(), 10, "diner@gmail.com"
        );
        assertThrows(ReservationValidationFailureException.class, () -> reservationValidationService.validateDateAndTime(reservationRequest));
    }

    @Test
    void validateDateAndTime_CurrentDatePastTime() {
        ReservationRequest reservationRequest = new ReservationRequest(
                1L, 1L, LocalTime.now().minusHours(1), LocalTime.now().plusHours(4), LocalDate.now(), 10, "diner@gmail.com"
        );
        assertThrows(ReservationValidationFailureException.class, () -> reservationValidationService.validateDateAndTime(reservationRequest));
    }

    @Test
    void validateDateAndTime_StartTimeAfterEndTime() {
        ReservationRequest reservationRequest = new ReservationRequest(
                1L, 1L, LocalTime.now(), LocalTime.now().minusHours(1), LocalDate.now(), 10, "diner@gmail.com"
        );
        assertThrows(ReservationValidationFailureException.class, () -> reservationValidationService.validateDateAndTime(reservationRequest));
    }

    @Test
    void validateRoomCapacity_greaterThanMax() {
        Room room = getRoom();
        assertThrows(ReservationValidationFailureException.class, () -> reservationValidationService.validateRoomCapacity(room, 20));
    }

    @Test
    void validateRoomCapacity_LessThanMin() {
        Room room = getRoom();
        assertThrows(ReservationValidationFailureException.class, () -> reservationValidationService.validateRoomCapacity(room, 9));
    }

    @Test
    void getReservationsByRoomReservationDateAndStatus() {
        Reservation reservation = getReservation();
        Room room = getRoom();
        when(reservationRepository.findByRoomReservationDateAndStatus(any(Room.class), any(LocalDate.class), eq(ReservationStatus.CONFIRMED))).thenReturn(List.of(reservation));
        List<Reservation> reservationList = reservationValidationService.getReservationsByRoomReservationDateAndStatus(room, LocalDate.now());
        assertNotNull(reservationList);
        assertEquals(1, reservationList.size());
        verify(reservationRepository, times(1)).findByRoomReservationDateAndStatus(any(Room.class), any(LocalDate.class), eq(ReservationStatus.CONFIRMED));
    }
}