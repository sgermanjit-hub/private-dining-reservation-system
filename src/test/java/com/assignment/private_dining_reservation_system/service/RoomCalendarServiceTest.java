package com.assignment.private_dining_reservation_system.service;

import com.assignment.private_dining_reservation_system.BaseTest;
import com.assignment.private_dining_reservation_system.entity.Room;
import com.assignment.private_dining_reservation_system.entity.RoomCalendar;
import com.assignment.private_dining_reservation_system.exception.EntityNotFoundException;
import com.assignment.private_dining_reservation_system.repository.RoomCalendarRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RoomCalendarServiceTest extends BaseTest {
    @Mock
    RoomCalendarRepository roomCalendarRepository;

    @InjectMocks
    RoomCalendarService roomCalendarService;

    @Test
    void lockRoomCalendarForReservationDate() {
        Room room = getRoom();
        RoomCalendar roomCalendar = getRoomCalendar();
        when(roomCalendarRepository.findByRoomAndReservationDateForUpdate(any(Room.class), any(LocalDate.class))).thenReturn(Optional.of(roomCalendar));
        RoomCalendar roomCalendarResult = roomCalendarService.lockRoomCalendarForReservationDate(room, LocalDate.now());
        assertNotNull(roomCalendarResult);
        assertEquals(roomCalendar.getReservationDate(), roomCalendarResult.getReservationDate());
        verify(roomCalendarRepository, times(1)).findByRoomAndReservationDateForUpdate(any(Room.class), any(LocalDate.class));
        verifyNoMoreInteractions(roomCalendarRepository);
    }

    @Test
    void lockRoomCalendarForReservationDate_RoomCalendarCreatedRealTime() {
        Room room = getRoom();
        RoomCalendar roomCalendar = getRoomCalendar();
        when(roomCalendarRepository.findByRoomAndReservationDateForUpdate(any(Room.class), any(LocalDate.class))).thenReturn(Optional.empty());
        when(roomCalendarRepository.saveAndFlush(any(RoomCalendar.class))).thenReturn(roomCalendar);
        RoomCalendar roomCalendarResult = roomCalendarService.lockRoomCalendarForReservationDate(room, LocalDate.now());
        assertNotNull(roomCalendarResult);
        assertEquals(roomCalendar.getReservationDate(), roomCalendarResult.getReservationDate());
        verify(roomCalendarRepository, times(1)).findByRoomAndReservationDateForUpdate(any(Room.class), any(LocalDate.class));
        verify(roomCalendarRepository, times(1)).saveAndFlush(any(RoomCalendar.class));
        verifyNoMoreInteractions(roomCalendarRepository);
    }

    @Test
    void lockRoomCalendarForReservationDate_AlreadyCreatedByOtherThreadException() {
        Room room = getRoom();
        when(roomCalendarRepository.findByRoomAndReservationDateForUpdate(any(Room.class), any(LocalDate.class))).thenReturn(Optional.empty());
        when(roomCalendarRepository.saveAndFlush(any(RoomCalendar.class))).thenThrow(new DataIntegrityViolationException("Already created row"));
        assertThrows(EntityNotFoundException.class, () -> roomCalendarService.lockRoomCalendarForReservationDate(room, LocalDate.now()));
        verify(roomCalendarRepository, times(2)).findByRoomAndReservationDateForUpdate(any(Room.class), any(LocalDate.class));
        verify(roomCalendarRepository, times(1)).saveAndFlush(any(RoomCalendar.class));
    }
}