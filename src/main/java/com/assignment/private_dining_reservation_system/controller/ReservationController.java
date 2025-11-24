package com.assignment.private_dining_reservation_system.controller;

import com.assignment.private_dining_reservation_system.entity.Reservation;
import com.assignment.private_dining_reservation_system.exception.ReservationValidationFailureException;
import com.assignment.private_dining_reservation_system.mapper.ReservationMapper;
import com.assignment.private_dining_reservation_system.model.request.AutoAssignReservationRequest;
import com.assignment.private_dining_reservation_system.model.request.ReservationRequest;
import com.assignment.private_dining_reservation_system.model.response.ReservationResponse;
import com.assignment.private_dining_reservation_system.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservationMapper reservationMapper;

    public ReservationController(ReservationService reservationService, ReservationMapper reservationMapper) {
        this.reservationService = reservationService;
        this.reservationMapper = reservationMapper;
    }

    /**
     * Endpoint to reserve a room for a specific room id for that restaurant
     *
     * @param reservationRequest reservationRequest
     * @return
     */
    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(@Valid @RequestBody ReservationRequest reservationRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reservationMapper.toResponse(reservationService.createReservation(reservationRequest)));
    }

    /**
     * Endpoint to book a Room based on room type and restaurant id
     * System will auto assign the room.
     *
     * @param autoAssignReservationRequest autoAssignReservationRequest
     * @return
     */
    @PostMapping("/auto-assign")
    public ResponseEntity<ReservationResponse> autoAssignCreateReservation(@Valid @RequestBody AutoAssignReservationRequest autoAssignReservationRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reservationMapper.toResponse(reservationService.autoAssignCreateReservation(autoAssignReservationRequest)));
    }

    /**
     * Endpoint to list all reservations based on diner or restaurant or both
     *
     * @param dinerEmail   dinerEmail
     * @param restaurantId restaurantId
     * @return
     */
    @GetMapping
    public ResponseEntity<List<ReservationResponse>> listReservations(@RequestParam(required = false) String dinerEmail,
                                                                      @RequestParam(required = false) Long restaurantId
    ) {
        List<Reservation> reservationList;
        if (null != dinerEmail && null != restaurantId) {
            reservationList = reservationService.getReservationByDinerAndRestaurantId(dinerEmail, restaurantId);
        } else if (null != dinerEmail) {
            reservationList = reservationService.getReservationsByDiner(dinerEmail);
        } else if (null != restaurantId) {
            reservationList = reservationService.getReservationByRestaurantId(restaurantId);
        } else {
            throw new ReservationValidationFailureException("Either Diner Email or Restaurant Id is mandatory for fetch reservations");
        }
        List<ReservationResponse> reservationResponseList = reservationList.stream().map(reservationMapper::toResponse).toList();
        return ResponseEntity.ok(reservationResponseList);
    }

    /**
     * Endpoint is used to cancel the reservation for reservation id
     * Need to implement staff or Diner role based access and then cancel the appropriate reservation
     * Considering user and role scope out of scope due to time,
     * considering right person is doing the cancellation
     *
     * @param reservationId reservationId
     * @return
     */
    @DeleteMapping("/cancel/{id}")
    public ResponseEntity<Void> cancelreservation(@PathVariable("id") Long reservationId) {
        /**
         * Likely steps: Fetch user role
         * Staff: find which restaurant it belongs to and allow cancellation of only those reservations
         * Diner: check with dinerEmail is it matches then allow to perform cancellations
         * Soft delete is better to keep track of reservations created in the system.
         * */
        reservationService.cancelReservation(reservationId);
        return ResponseEntity.noContent().build();
    }
}
