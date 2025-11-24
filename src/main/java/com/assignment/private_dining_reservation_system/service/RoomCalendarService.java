package com.assignment.private_dining_reservation_system.service;

import com.assignment.private_dining_reservation_system.entity.Room;
import com.assignment.private_dining_reservation_system.entity.RoomCalendar;
import com.assignment.private_dining_reservation_system.exception.EntityNotFoundException;
import com.assignment.private_dining_reservation_system.repository.RoomCalendarRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class RoomCalendarService {

    private final RoomCalendarRepository roomCalendarRepository;

    public RoomCalendarService(RoomCalendarRepository roomCalendarRepository) {
        this.roomCalendarRepository = roomCalendarRepository;
    }

    public RoomCalendar lockRoomCalendarForReservationDate(Room room, LocalDate reservationDate) {
        return roomCalendarRepository.findByRoomAndReservationDateForUpdate(room, reservationDate)
                .orElseGet(() -> {
                    RoomCalendar newRoomCalendar = new RoomCalendar();
                    newRoomCalendar.setReservationDate(reservationDate);
                    newRoomCalendar.setRoom(room);
                    try {
                        return roomCalendarRepository.saveAndFlush(newRoomCalendar);
                    } catch (DataIntegrityViolationException dataIntegrityViolationException) {
                        return roomCalendarRepository.findByRoomAndReservationDateForUpdate(room, reservationDate)
                                .orElseThrow(() -> new EntityNotFoundException("Unable to acquire lock as other transaction might have consumed it."));
                    }
                });
    }
}
