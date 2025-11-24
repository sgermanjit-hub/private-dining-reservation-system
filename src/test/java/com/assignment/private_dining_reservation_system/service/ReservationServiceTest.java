package com.assignment.private_dining_reservation_system.service;

import com.assignment.private_dining_reservation_system.BaseTest;
import com.assignment.private_dining_reservation_system.entity.*;
import com.assignment.private_dining_reservation_system.exception.ReservationFailedException;
import com.assignment.private_dining_reservation_system.exception.RoomNotAvailableException;
import com.assignment.private_dining_reservation_system.model.request.AutoAssignReservationRequest;
import com.assignment.private_dining_reservation_system.model.request.ReservationRequest;
import com.assignment.private_dining_reservation_system.model.request.ReservationTimeFrame;
import com.assignment.private_dining_reservation_system.repository.ReservationRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class ReservationServiceTest extends BaseTest {

    @Mock
    RestaurantAvailabilityService restaurantAvailabilityService;
    @Mock
    RoomService roomService;
    @Mock
    RestaurantService restaurantService;
    @Mock
    RoomCalendarService roomCalendarService;
    @Mock
    ReservationValidationService reservationValidationService;
    @Mock
    ReservationEventProducer reservationEventProducer;
    @Mock
    ReservationRepository reservationRepository;
    @InjectMocks
    ReservationService reservationService;

    @Test
    void createReservation() {
        ReservationRequest reservationRequest = getReservationRequest();
        Restaurant restaurant = getRestaurant();
        Room room = getRoom();
        Reservation reservation = getReservation();
        RoomCalendar roomCalendar = getRoomCalendar();
        when(roomService.getByIdAndRestaurantId(anyLong(), anyLong())).thenReturn(room);
        when(restaurantService.getById(anyLong())).thenReturn(restaurant);
        doNothing().when(reservationEventProducer).sendReservationEvent(any(Reservation.class));
        when(reservationValidationService.checkOverlap(any(Room.class), any(LocalDate.class), any(LocalTime.class), any(LocalTime.class))).thenReturn(false);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);
        when(roomCalendarService.lockRoomCalendarForReservationDate(any(Room.class), any(LocalDate.class))).thenReturn(roomCalendar);
        Reservation reservationResult = reservationService.createReservation(reservationRequest);
        assertEquals(reservation.getId(), reservationResult.getId());
        assertEquals(reservation.getReservationStatus(), reservationResult.getReservationStatus());
        assertEquals(reservation.getRoom(), reservationResult.getRoom());
        assertEquals(reservation.getRestaurant(), reservationResult.getRestaurant());
        assertEquals(reservation.getReservationDate(), reservationResult.getReservationDate());
        assertEquals(reservation.getReservationStartTime(), reservationResult.getReservationStartTime());
        assertEquals(reservation.getReservationEndTime(), reservationResult.getReservationEndTime());
        assertEquals(reservation.getGroupSize(), reservationResult.getGroupSize());
        assertEquals(reservation.getDinerEmail(), reservationResult.getDinerEmail());
        verify(roomService, times(1)).getByIdAndRestaurantId(anyLong(), anyLong());
        verify(restaurantService, times(1)).getById(anyLong());
        verify(reservationEventProducer, times(1)).sendReservationEvent(any(Reservation.class));
        verify(reservationValidationService, times(1)).checkOverlap(any(Room.class), any(LocalDate.class), any(LocalTime.class), any(LocalTime.class));
        verify(reservationRepository, times(1)).save(any(Reservation.class));
        verify(roomCalendarService, times(1)).lockRoomCalendarForReservationDate(any(Room.class), any(LocalDate.class));
    }

    @Test
    void createReservation_OverlapException() {
        ReservationRequest reservationRequest = getReservationRequest();
        Restaurant restaurant = getRestaurant();
        Room room = getRoom();
        Reservation reservation = getReservation();
        RoomCalendar roomCalendar = getRoomCalendar();
        when(roomService.getByIdAndRestaurantId(anyLong(), anyLong())).thenReturn(room);
        when(restaurantService.getById(anyLong())).thenReturn(restaurant);
        when(reservationValidationService.checkOverlap(any(Room.class), any(LocalDate.class), any(LocalTime.class), any(LocalTime.class))).thenReturn(true);
        when(roomCalendarService.lockRoomCalendarForReservationDate(any(Room.class), any(LocalDate.class))).thenReturn(roomCalendar);
        assertThrows(RoomNotAvailableException.class, () -> reservationService.createReservation(reservationRequest));
        verify(roomService, times(1)).getByIdAndRestaurantId(anyLong(), anyLong());
        verify(restaurantService, times(1)).getById(anyLong());
        verifyNoInteractions(reservationEventProducer);
        verify(reservationValidationService, times(1)).checkOverlap(any(Room.class), any(LocalDate.class), any(LocalTime.class), any(LocalTime.class));
        verifyNoInteractions(reservationRepository);
        verify(roomCalendarService, times(1)).lockRoomCalendarForReservationDate(any(Room.class), any(LocalDate.class));
    }

    @Test
    void autoAssignCreateReservation() {
        AutoAssignReservationRequest autoAssignReservationRequest = getAutoAssignReservationRequest();
        Restaurant restaurant = getRestaurant();
        Room room = getRoom();
        List<Room> roomList = List.of(room);
        Reservation reservation = getReservation();
        RoomCalendar roomCalendar = getRoomCalendar();
        when(restaurantAvailabilityService.findAvailableRoomsByRestaurantAndRoomType(anyLong(), any(RoomType.class), any(ReservationTimeFrame.class))).thenReturn(roomList);
        when(restaurantService.getById(anyLong())).thenReturn(restaurant);
        doNothing().when(reservationEventProducer).sendReservationEvent(any(Reservation.class));
        when(reservationValidationService.checkOverlap(any(Room.class), any(LocalDate.class), any(LocalTime.class), any(LocalTime.class))).thenReturn(false);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);
        when(roomCalendarService.lockRoomCalendarForReservationDate(any(Room.class), any(LocalDate.class))).thenReturn(roomCalendar);
        Reservation reservationResult = reservationService.autoAssignCreateReservation(autoAssignReservationRequest);
        assertEquals(reservation.getId(), reservationResult.getId());
        assertEquals(reservation.getReservationStatus(), reservationResult.getReservationStatus());
        assertEquals(reservation.getRoom(), reservationResult.getRoom());
        assertEquals(reservation.getRestaurant(), reservationResult.getRestaurant());
        assertEquals(reservation.getReservationDate(), reservationResult.getReservationDate());
        assertEquals(reservation.getReservationStartTime(), reservationResult.getReservationStartTime());
        assertEquals(reservation.getReservationEndTime(), reservationResult.getReservationEndTime());
        assertEquals(reservation.getGroupSize(), reservationResult.getGroupSize());
        assertEquals(reservation.getDinerEmail(), reservationResult.getDinerEmail());
        verify(restaurantService, times(1)).getById(anyLong());
        verify(reservationEventProducer, times(1)).sendReservationEvent(any(Reservation.class));
        verify(reservationValidationService, times(1)).checkOverlap(any(Room.class), any(LocalDate.class), any(LocalTime.class), any(LocalTime.class));
        verify(reservationRepository, times(1)).save(any(Reservation.class));
        verify(roomCalendarService, times(1)).lockRoomCalendarForReservationDate(any(Room.class), any(LocalDate.class));
        verify(restaurantAvailabilityService).findAvailableRoomsByRestaurantAndRoomType(anyLong(), any(RoomType.class), any(ReservationTimeFrame.class));
    }

    @Test
    void autoAssignCreateReservation_RoomNotAvailableByRoomTypeException() {
        AutoAssignReservationRequest autoAssignReservationRequest = getAutoAssignReservationRequest();
        when(restaurantAvailabilityService.findAvailableRoomsByRestaurantAndRoomType(anyLong(), any(RoomType.class), any(ReservationTimeFrame.class))).thenReturn(Collections.emptyList());
        assertThrows(RoomNotAvailableException.class, () -> reservationService.autoAssignCreateReservation(autoAssignReservationRequest));
        verifyNoInteractions(restaurantService);
        verifyNoInteractions(reservationEventProducer);
        verifyNoInteractions(reservationValidationService);
        verifyNoInteractions(reservationRepository);
        verify(restaurantAvailabilityService, times(1)).findAvailableRoomsByRestaurantAndRoomType(anyLong(), any(RoomType.class), any(ReservationTimeFrame.class));
    }

    @Test
    void autoAssignCreateReservation_RoomCapacityMismatchException() {
        AutoAssignReservationRequest autoAssignReservationRequest = getAutoAssignReservationRequest();
        Room room = getRoom();
        room.setMinCapacity(20);
        room.setMaxCapacity(22);
        List<Room> roomList = List.of(room);
        when(restaurantAvailabilityService.findAvailableRoomsByRestaurantAndRoomType(anyLong(), any(RoomType.class), any(ReservationTimeFrame.class))).thenReturn(roomList);
        assertThrows(RoomNotAvailableException.class, () -> reservationService.autoAssignCreateReservation(autoAssignReservationRequest));
        verifyNoInteractions(restaurantService);
        verifyNoInteractions(reservationEventProducer);
        verifyNoInteractions(reservationValidationService);
        verifyNoInteractions(reservationRepository);
        verifyNoInteractions(roomCalendarService);
        verify(restaurantAvailabilityService, times(1)).findAvailableRoomsByRestaurantAndRoomType(anyLong(), any(RoomType.class), any(ReservationTimeFrame.class));

    }

    @Test
    void getReservationsByDiner() {
        Reservation reservation = getReservation();
        List<Reservation> reservationList = List.of(reservation);
        when(reservationRepository.findByDinerEmail(anyString())).thenReturn(reservationList);
        List<Reservation> reservationListResult = reservationService.getReservationsByDiner("diner@gmail.com");
        assertEquals(reservationList.size(), reservationListResult.size());
        assertEquals(reservationList.getFirst().getRoom(), reservationListResult.getFirst().getRoom());
        assertEquals(reservationList.getFirst().getGroupSize(), reservationListResult.getFirst().getGroupSize());
        verify(reservationRepository, times(1)).findByDinerEmail(anyString());
    }

    @Test
    void getReservationByRestaurantId() {
        Reservation reservation = getReservation();
        List<Reservation> reservationList = List.of(reservation);
        when(reservationRepository.findByRestaurantId(anyLong())).thenReturn(reservationList);
        List<Reservation> reservationListResult = reservationService.getReservationByRestaurantId(1L);
        assertEquals(reservationList.size(), reservationListResult.size());
        assertEquals(reservationList.getFirst().getRoom(), reservationListResult.getFirst().getRoom());
        assertEquals(reservationList.getFirst().getGroupSize(), reservationListResult.getFirst().getGroupSize());
        verify(reservationRepository, times(1)).findByRestaurantId(anyLong());
    }

    @Test
    void getReservationByDinerAndRestaurantId() {
        Reservation reservation = getReservation();
        List<Reservation> reservationList = List.of(reservation);
        when(reservationRepository.findByDinerEmailAndRestaurantId(anyString(), anyLong())).thenReturn(reservationList);
        List<Reservation> reservationListResult = reservationService.getReservationByDinerAndRestaurantId("diner@gmail.com", 1L);
        assertEquals(reservationList.size(), reservationListResult.size());
        assertEquals(reservationList.getFirst().getRoom(), reservationListResult.getFirst().getRoom());
        assertEquals(reservationList.getFirst().getGroupSize(), reservationListResult.getFirst().getGroupSize());
        verify(reservationRepository, times(1)).findByDinerEmailAndRestaurantId(anyString(), anyLong());
    }

    @Test
    void cancelReservation() {
        Reservation reservation = getReservation();
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);
        reservationService.cancelReservation(1L);
        verify(reservationRepository, times(1)).findById(anyLong());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    void cancelReservation_alreadyCancelled() {
        Reservation reservation = getReservation();
        reservation.setReservationStatus(ReservationStatus.CANCELLED);
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.of(reservation));
        assertThrows(ReservationFailedException.class, () -> reservationService.cancelReservation(1L));
        verify(reservationRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(reservationRepository);
    }
}