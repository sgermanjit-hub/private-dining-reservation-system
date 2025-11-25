package com.assignment.private_dining_reservation_system.service;

import com.assignment.private_dining_reservation_system.constants.Constants;
import com.assignment.private_dining_reservation_system.entity.Reservation;
import com.assignment.private_dining_reservation_system.entity.ReservationStatus;
import com.assignment.private_dining_reservation_system.entity.Room;
import com.assignment.private_dining_reservation_system.entity.RoomMetaData;
import com.assignment.private_dining_reservation_system.exception.ReservationValidationFailureException;
import com.assignment.private_dining_reservation_system.model.request.ReservationRequest;
import com.assignment.private_dining_reservation_system.repository.ReservationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.List;
/**
 * Central place for all reservation validations
 * */
@Slf4j
@Service
public class ReservationValidationService {
    private final ReservationRepository reservationRepository;

    public ReservationValidationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    /**
     * Check if overlapping reservations exists
     * This check kept at an application level so that we are not restricting ourselves to db
     * Keeping logic in application will help us to move to any db in future
     * Secondly, based on private dining which is definitely booking for little larger time, the number of rows per day per room will be less
     * hence easy to keep the logic here
     *
     *
     * @param room, reservationDate, startTime, endTime
     * @return boolean
     *
     */
    public boolean checkOverlap(Room room, LocalDate reservationDate, LocalTime startTime, LocalTime endTime) {
        //1. Get all reservations of same day to check overlap
        List<Reservation> reservationsForSameDay = getReservationsByRoomReservationDateAndStatus(room, reservationDate);

        LocalDateTime newReservationTime = reservationDate.atTime(startTime);
        LocalDateTime newReservationEndTime = getFinalEndDateTime(reservationDate, startTime, endTime);
        //2. Verify any overlap with existing reservations
        for (Reservation reservation : reservationsForSameDay) {
            LocalTime existingReservationStartTime = reservation.getReservationStartTime();
            LocalTime existingReservationEndTime = reservation.getReservationEndTime();

            LocalDate existingReservationDate = reservation.getReservationDate();
            LocalDateTime existingStart = existingReservationDate.atTime(existingReservationStartTime);
            LocalDateTime existingEnd = getFinalEndDateTime(existingReservationDate, existingReservationStartTime, existingReservationEndTime);
            boolean overlap = newReservationTime.isBefore(existingEnd)
                    && newReservationEndTime.isAfter(existingStart);

            if (overlap) {
                // 3. Overlap Exists -> Don't confirm this reservation.
                return true;
            }
        }
        return false;
    }

    /**
     * There is a possibility of room reservation overlap with the next day time interval
     * Hence end Time will be decided considering the day change.
     * */
    private LocalDateTime getFinalEndDateTime(LocalDate date, LocalTime start, LocalTime end) {
        //Midnight crossed
        if (!end.isAfter(start)) {
            return date.plusDays(1).atTime(end);
        }
        return date.atTime(end);
    }

    /**
     * validate Date and Time for Reservation
     * Reservation End Time > Reservation Start TIme
     * Min Booking is for 3 hours
     * Max Booking allowed is for 30 days
     *
     */
    public void validateDateAndTime(ReservationRequest reservationRequest) {
        LocalDate today = LocalDate.now();
        LocalDate reservationDate = reservationRequest.reservationDate();
        LocalTime start = reservationRequest.reservationStartTime();
        LocalTime end = reservationRequest.reservationEndTime();

        LocalDateTime reservationStartTime = reservationDate.atTime(start);
        LocalDateTime reservationEndTime = getFinalEndDateTime(reservationDate, start, end);

        if (reservationDate.isBefore(today) || reservationDate.isAfter(today.plusDays(Constants.advanceBookingDays))) {
            throw new ReservationValidationFailureException("Reservation date should be greater than today and less than 30 days in advance.");
        }

        if (reservationDate.isEqual(today) && !reservationStartTime.isAfter(LocalDateTime.now())) {
            throw new ReservationValidationFailureException("Reservation Start Time should be greater than current for current day bookings.");
        }
        if (!reservationEndTime.isAfter(reservationStartTime)) {
            throw new ReservationValidationFailureException("Start Time cannot be earlier than end Time");
        }

        long hours = Duration.between(reservationStartTime, reservationEndTime).toHours();
        if (hours < Constants.minBookingHours) {
            throw new ReservationValidationFailureException("Min Booking for Private Dining is for 3 hours");
        }
    }

    /**
     * Validate Room can serve Group Size
     *
     */
    public void validateRoomCapacity(Room room, int groupSize) {
        if (groupSize > room.getMaxCapacity() || groupSize < room.getMinCapacity()) {
            throw new ReservationValidationFailureException("Room is not suitable for your group");
        }
    }

    /**
     * Validate whether reservation is within the opening closing time
     * */
    public boolean validateRoomOperatingHours(Room room, LocalDate reservationDate, LocalTime startTime, LocalTime endTime) {
        RoomMetaData roomMetaData = room.getRoomMetaData();
        if(roomMetaData == null){
            log.debug("Room is not available");
            return false;
        }
        //Check room is operating on that day of week
        if(null != roomMetaData.getOpenDays() && !roomMetaData.getOpenDays().isEmpty()){
            DayOfWeek dayOfWeek = reservationDate.getDayOfWeek();
            if(!roomMetaData.getOpenDays().contains(dayOfWeek)){
                log.debug("Room is not available on the request day");
                return false;
            }
        }
        LocalDateTime reservationStartTime = reservationDate.atTime(startTime);
        LocalDateTime reservationEndTime = getFinalEndDateTime(reservationDate, startTime, endTime);

        LocalDateTime openReservationTime = reservationDate.atTime(roomMetaData.getRoomOpeningTime());
        LocalDateTime closeReservationEndTime = getFinalEndDateTime(reservationDate, roomMetaData.getRoomOpeningTime(), roomMetaData.getRoomClosingTime());


        //check room is operating at request start time
        if(reservationStartTime.isBefore(openReservationTime)){
            log.debug("Room opens at: " + roomMetaData.getRoomOpeningTime());
            return false;
        }

        //
        if(reservationEndTime.isAfter(closeReservationEndTime)){
            log.debug("Room closes at: " + roomMetaData.getRoomClosingTime());
            return false;
        }
        return true;
    }

    public List<Reservation> getReservationsByRoomReservationDateAndStatus(Room room, LocalDate reservationDate) {
        return reservationRepository.findByRoomReservationDateAndStatus(room, reservationDate, ReservationStatus.CONFIRMED);
    }
}
