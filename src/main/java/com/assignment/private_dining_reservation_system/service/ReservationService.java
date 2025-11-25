package com.assignment.private_dining_reservation_system.service;

import com.assignment.private_dining_reservation_system.entity.*;
import com.assignment.private_dining_reservation_system.exception.EntityNotFoundException;
import com.assignment.private_dining_reservation_system.exception.ReservationFailedException;
import com.assignment.private_dining_reservation_system.exception.RoomNotAvailableException;
import com.assignment.private_dining_reservation_system.model.request.AutoAssignReservationRequest;
import com.assignment.private_dining_reservation_system.model.request.ReservationRequest;
import com.assignment.private_dining_reservation_system.model.request.ReservationTimeFrame;
import com.assignment.private_dining_reservation_system.repository.ReservationRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
/**
 * It basically orchestrates and manage the full lifecycle of the reservation workflow.
 * */
@Slf4j
@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final RoomService roomService;
    private final RestaurantService restaurantService;
    private final RoomCalendarService roomCalendarService;
    private final ReservationValidationService reservationValidationService;
    private final RestaurantAvailabilityService restaurantAvailabilityService;
    private final ReservationEventProducer reservationEventProducer;

    public ReservationService(ReservationRepository reservationRepository, RoomService roomService, RestaurantService restaurantService, RoomCalendarService roomCalendarService, ReservationValidationService reservationValidationService, RestaurantAvailabilityService restaurantAvailabilityService, ReservationEventProducer reservationEventProducer) {
        this.reservationRepository = reservationRepository;
        this.roomService = roomService;
        this.restaurantService = restaurantService;
        this.roomCalendarService = roomCalendarService;
        this.reservationValidationService = reservationValidationService;
        this.restaurantAvailabilityService = restaurantAvailabilityService;
        this.reservationEventProducer = reservationEventProducer;
    }

    @Transactional
    public Reservation createReservation(@Valid ReservationRequest reservationRequest) {
        //1. Fetch Room Details
        Room room = roomService.getByIdAndRestaurantId(reservationRequest.roomId(), reservationRequest.restaurantId());
        //2. Fetch Restaurant Details
        Restaurant restaurant = restaurantService.getById(reservationRequest.restaurantId());
        //3. Validate Reservation is within room operating hours
        boolean validOperatingHours = reservationValidationService.validateRoomOperatingHours(room, reservationRequest.reservationDate(), reservationRequest.reservationStartTime(), reservationRequest.reservationEndTime());
        if(!validOperatingHours){
            throw new RoomNotAvailableException("Reservation is not within operating hours");
        }
        //4. Validate Date and Time For Reservation
        //   # Reservation Date >= today
        //   # Reservation end time > reservation start time
        reservationValidationService.validateDateAndTime(reservationRequest);
        //5. Validate Room Capacity for Group
        reservationValidationService.validateRoomCapacity(room, reservationRequest.groupSize());
        //6. Core Reservation Logic
        Reservation reservation = createRoomReservation(restaurant, room, reservationRequest);
        //7. Emit Notification for reservation created
        reservationEventProducer.sendReservationEvent(reservation);
        return reservation;
    }

    @Transactional
    public Reservation autoAssignCreateReservation(@Valid AutoAssignReservationRequest autoAssignReservationRequest) {
        //1. Fetch Available Room Details for restaurant and Room Type
        List<Room> rooms = restaurantAvailabilityService.findAvailableRoomsByRestaurantAndRoomType(autoAssignReservationRequest.restaurantId(), autoAssignReservationRequest.roomType(), new ReservationTimeFrame(
                autoAssignReservationRequest.reservationDate(),
                autoAssignReservationRequest.reservationStartTime(),
                autoAssignReservationRequest.reservationEndTime()
        ));

        if (CollectionUtils.isEmpty(rooms)) {
            String errorMessage = String.format("No Room is available for Restaurant id: %d  Reservation date: %s Room Type: %s",
                    autoAssignReservationRequest.restaurantId(), autoAssignReservationRequest.reservationDate(), autoAssignReservationRequest.roomType());
            log.error(errorMessage);
            throw new RoomNotAvailableException(errorMessage);
        }

        //2. Select any one room from available rooms
        Room selectedRoom = rooms.stream()
                .filter(room -> autoAssignReservationRequest.groupSize() >= room.getMinCapacity()
                        && autoAssignReservationRequest.groupSize() <= room.getMaxCapacity())
                .findFirst()
                .orElseThrow(() -> new RoomNotAvailableException("No room is available to accommodate this group size"));


        //3. Fetch Restaurant Details
        Restaurant restaurant = restaurantService.getById(autoAssignReservationRequest.restaurantId());

        //4. Create reservation request Payload with selected room id
        ReservationRequest reservationRequest = new ReservationRequest(
                selectedRoom.getId(),
                autoAssignReservationRequest.restaurantId(),
                autoAssignReservationRequest.reservationStartTime(),
                autoAssignReservationRequest.reservationEndTime(),
                autoAssignReservationRequest.reservationDate(),
                autoAssignReservationRequest.groupSize(),
                autoAssignReservationRequest.dinerEmail()
        );
        //5. Validate Reservation is within room operating hours
        boolean validOperatingHours = reservationValidationService.validateRoomOperatingHours(selectedRoom, reservationRequest.reservationDate(), reservationRequest.reservationStartTime(), reservationRequest.reservationEndTime());
        if(!validOperatingHours){
            throw new RoomNotAvailableException("Reservation is not within operating hours");
        }
        //6. Validate Date and Time For Reservation
        //   # Reservation Date >= today
        //   # Reservation end time > reservation start time
        reservationValidationService.validateDateAndTime(reservationRequest);

        //7. Core Reservation Logic
        Reservation reservation = createRoomReservation(restaurant, selectedRoom, reservationRequest);
        //7. Emit Notification for reservation created
        reservationEventProducer.sendReservationEvent(reservation);
        return reservation;
    }

    private Reservation createRoomReservation(Restaurant restaurant, Room room, ReservationRequest reservationRequest) {
        //1. Lock Room Calendar to prevent Double Booking
        LocalDate reservationDate = reservationRequest.reservationDate();
        RoomCalendar roomCalendar = lockRoomCalendarForReservationDate(room, reservationDate);

        //2. Verify any overlap with existing reservations
        boolean overlap = reservationValidationService.checkOverlap(room, reservationDate, reservationRequest.reservationStartTime(), reservationRequest.reservationEndTime());

        //3. If overlap, fail the reservation
        if (overlap) {
            throw new RoomNotAvailableException("Room is not available for reservation at give time range");
        }

        //4. Create Reservation Payload
        Reservation reservation = createReservationPayload(reservationRequest, restaurant, room);

        //5. Create Reservation Record
        return reservationRepository.save(reservation);
    }

    public List<Reservation> getReservationsByDiner(String email) {
        return reservationRepository.findByDinerEmail(email);
    }

    public List<Reservation> getReservationByRestaurantId(Long restaurantId) {
        return reservationRepository.findByRestaurantId(restaurantId);
    }

    public List<Reservation> getReservationByDinerAndRestaurantId(String dinerEmail, Long restaurantId) {
        return reservationRepository.findByDinerEmailAndRestaurantId(dinerEmail, restaurantId);
    }

    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found with reservation id: " + reservationId));

        LocalDateTime reservationDateTime = LocalDateTime.of(reservation.getReservationDate(), reservation.getReservationStartTime());
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(reservationDateTime.minusHours(24))) {
            throw new ReservationFailedException("Reservation can only be cancelled at least 24 hours in advance.");
        }
        if (ReservationStatus.CANCELLED == reservation.getReservationStatus()) {
            throw new ReservationFailedException("Reservation is already cancelled");
        }

        reservation.setReservationStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
    }

    private RoomCalendar lockRoomCalendarForReservationDate(Room room, LocalDate reservationDate) {
        return roomCalendarService.lockRoomCalendarForReservationDate(room, reservationDate);
    }

    private Reservation createReservationPayload(ReservationRequest reservationRequest, Restaurant restaurant, Room room) {
        Reservation reservation = new Reservation();
        reservation.setRestaurant(restaurant);
        reservation.setRoom(room);
        reservation.setReservationDate(reservationRequest.reservationDate());
        reservation.setGroupSize(reservationRequest.groupSize());
        reservation.setDinerEmail(reservationRequest.dinerEmail());
        reservation.setReservationStartTime(reservationRequest.reservationStartTime());
        reservation.setReservationEndTime(reservationRequest.reservationEndTime());
        reservation.setReservationStatus(ReservationStatus.CONFIRMED);
        return reservation;
    }
}
