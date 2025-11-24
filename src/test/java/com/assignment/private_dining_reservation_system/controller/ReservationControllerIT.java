package com.assignment.private_dining_reservation_system.controller;

import com.assignment.private_dining_reservation_system.entity.Reservation;
import com.assignment.private_dining_reservation_system.entity.ReservationStatus;
import com.assignment.private_dining_reservation_system.entity.Restaurant;
import com.assignment.private_dining_reservation_system.entity.Room;
import com.assignment.private_dining_reservation_system.mapper.ReservationMapper;
import com.assignment.private_dining_reservation_system.model.request.AutoAssignReservationRequest;
import com.assignment.private_dining_reservation_system.model.request.ReservationRequest;
import com.assignment.private_dining_reservation_system.model.response.ReservationResponse;
import com.assignment.private_dining_reservation_system.repository.ReservationRepository;
import com.assignment.private_dining_reservation_system.repository.RestaurantRepository;
import com.assignment.private_dining_reservation_system.repository.RoomRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@Transactional
public class ReservationControllerIT extends BaseIntegrationTest {
    @Autowired
    ReservationRepository reservationRepository;
    @Autowired
    RestaurantRepository restaurantRepository;
    @Autowired
    RoomRepository roomRepository;
    @Autowired
    ReservationMapper reservationMapper;

    @Test
    void createReservation_whenRestaurantAndRoomAlreadyCreated() throws Exception {
        Restaurant restaurantRequest = getRestaurant();
        Restaurant restaurant = restaurantRepository.save(restaurantRequest);

        Room roomPayload = getRoom();
        roomPayload.setRestaurant(restaurant);
        Room room = roomRepository.save(roomPayload);

        ReservationRequest reservationRequest = getReservationRequest();
        String url = "/api/reservations";

        ResultActions result = performPost(url, reservationRequest, HttpStatus.CREATED)
                .andExpect(jsonPath("$.roomId").exists())
                .andExpect(jsonPath("$.restaurantId").exists())
                .andExpect(jsonPath("$.roomName").exists())
                .andExpect(jsonPath("$.reservationDate").exists())
                .andExpect(jsonPath("$.reservationStartTime").exists())
                .andExpect(jsonPath("$.reservationEndTime").exists())
                .andExpect(jsonPath("$.reservationStatus").exists())
                .andExpect(jsonPath("$.dinerEmail").exists())
                .andExpect(jsonPath("$.groupSize").exists());

        String reservationResponse = result.andReturn().getResponse().getContentAsString();
        ReservationResponse reservationResponseResult = objectMapper.readValue(reservationResponse, ReservationResponse.class);
        Reservation existing = reservationRepository.findById(reservationResponseResult.id()).orElseThrow();
        ReservationResponse existingReservationResponse = reservationMapper.toResponse(existing);

        assertNotNull(reservationResponseResult);
        assertEquals(existingReservationResponse.roomId(), reservationResponseResult.roomId());
        assertEquals(existingReservationResponse.restaurantId(), reservationResponseResult.restaurantId());
        assertEquals(existingReservationResponse.roomName(), reservationResponseResult.roomName());
        assertEquals(existingReservationResponse.reservationDate(), reservationResponseResult.reservationDate());
        assertEquals(existingReservationResponse.reservationStartTime(), reservationResponseResult.reservationStartTime());
        assertEquals(existingReservationResponse.reservationEndTime(), reservationResponseResult.reservationEndTime());
        assertEquals(existingReservationResponse.reservationStatus(), reservationResponseResult.reservationStatus());
        assertEquals(existingReservationResponse.dinerEmail(), reservationResponseResult.dinerEmail());
        assertEquals(existingReservationResponse.groupSize(), reservationResponseResult.groupSize());
    }

    @Test
    void failedReservation_whenOverlappingReservationExistingEarlier() throws Exception {
        Restaurant restaurantRequest = getRestaurant();
        Restaurant restaurant = restaurantRepository.save(restaurantRequest);

        Room roomPayload = getRoom();
        roomPayload.setRestaurant(restaurant);
        Room room = roomRepository.save(roomPayload);

        Reservation reservation = getReservation(restaurant, room);
        reservation.setReservationDate(LocalDate.of(2025, 12, 20));
        reservationRepository.save(reservation);

        ReservationRequest reservationRequest = getReservationRequest();
        String url = "/api/reservations";
        ResultActions result = performPost(url, reservationRequest, HttpStatus.CONFLICT);

        String reservationResponse = result.andReturn().getResponse().getContentAsString();
        assertEquals("{\"error\":\"Room is not available for reservation at give time range\"}", reservationResponse);
    }

    @Test
    void createReservation_whenExistingConflictingReservationIsCancelled() throws Exception {
        Restaurant restaurantRequest = getRestaurant();
        Restaurant restaurant = restaurantRepository.save(restaurantRequest);

        Room roomPayload = getRoom();
        roomPayload.setRestaurant(restaurant);
        Room room = roomRepository.save(roomPayload);

        Reservation reservation = getReservation(restaurant, room);
        reservation.setReservationDate(LocalDate.of(2025, 12, 20));
        reservation.setReservationStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);

        ReservationRequest reservationRequest = getReservationRequest();
        String url = "/api/reservations";
        ResultActions result = performPost(url, reservationRequest, HttpStatus.CREATED)
                .andExpect(jsonPath("$.roomId").exists())
                .andExpect(jsonPath("$.restaurantId").exists())
                .andExpect(jsonPath("$.roomName").exists())
                .andExpect(jsonPath("$.reservationDate").exists())
                .andExpect(jsonPath("$.reservationStartTime").exists())
                .andExpect(jsonPath("$.reservationEndTime").exists())
                .andExpect(jsonPath("$.reservationStatus").exists())
                .andExpect(jsonPath("$.dinerEmail").exists())
                .andExpect(jsonPath("$.groupSize").exists());

        String reservationResponse = result.andReturn().getResponse().getContentAsString();
        ReservationResponse reservationResponseResult = objectMapper.readValue(reservationResponse, ReservationResponse.class);
        Reservation existing = reservationRepository.findById(reservationResponseResult.id()).orElseThrow();
        ReservationResponse existingReservationResponse = reservationMapper.toResponse(existing);
        assertNotNull(reservationResponseResult);
        assertEquals(existingReservationResponse.roomId(), reservationResponseResult.roomId());
        assertEquals(existingReservationResponse.restaurantId(), reservationResponseResult.restaurantId());
        assertEquals(existingReservationResponse.roomName(), reservationResponseResult.roomName());
        assertEquals(existingReservationResponse.reservationDate(), reservationResponseResult.reservationDate());
        assertEquals(existingReservationResponse.reservationStartTime(), reservationResponseResult.reservationStartTime());
        assertEquals(existingReservationResponse.reservationEndTime(), reservationResponseResult.reservationEndTime());
        assertEquals(existingReservationResponse.reservationStatus(), reservationResponseResult.reservationStatus());
        assertEquals(existingReservationResponse.dinerEmail(), reservationResponseResult.dinerEmail());
        assertEquals(existingReservationResponse.groupSize(), reservationResponseResult.groupSize());
    }

    @Test
    void createAutoAssignReservation_whenRestaurantAndRoomAlreadyCreated() throws Exception {
        Restaurant restaurantRequest = getRestaurant();
        Restaurant restaurant = restaurantRepository.save(restaurantRequest);

        Room roomPayload = getRoom();
        roomPayload.setRestaurant(restaurant);
        roomRepository.save(roomPayload);

        AutoAssignReservationRequest autoAssignReservationRequest = getAutoAssignReservationRequest();
        String url = "/api/reservations/auto-assign";
        ResultActions result = performPost(url, autoAssignReservationRequest, HttpStatus.CREATED)
                .andExpect(jsonPath("$.roomId").exists())
                .andExpect(jsonPath("$.restaurantId").exists())
                .andExpect(jsonPath("$.roomName").exists())
                .andExpect(jsonPath("$.reservationDate").exists())
                .andExpect(jsonPath("$.reservationStartTime").exists())
                .andExpect(jsonPath("$.reservationEndTime").exists())
                .andExpect(jsonPath("$.reservationStatus").exists())
                .andExpect(jsonPath("$.dinerEmail").exists())
                .andExpect(jsonPath("$.groupSize").exists());

        String reservationResponse = result.andReturn().getResponse().getContentAsString();
        ReservationResponse reservationResponseResult = objectMapper.readValue(reservationResponse, ReservationResponse.class);
        Reservation existing = reservationRepository.findById(reservationResponseResult.id()).orElseThrow();
        ReservationResponse existingReservationResponse = reservationMapper.toResponse(existing);
        assertNotNull(reservationResponseResult);
        assertEquals(existingReservationResponse.roomId(), reservationResponseResult.roomId());
        assertEquals(existingReservationResponse.restaurantId(), reservationResponseResult.restaurantId());
        assertEquals(existingReservationResponse.roomName(), reservationResponseResult.roomName());
        assertEquals(existingReservationResponse.reservationDate(), reservationResponseResult.reservationDate());
        assertEquals(existingReservationResponse.reservationStartTime(), reservationResponseResult.reservationStartTime());
        assertEquals(existingReservationResponse.reservationEndTime(), reservationResponseResult.reservationEndTime());
        assertEquals(existingReservationResponse.reservationStatus(), reservationResponseResult.reservationStatus());
        assertEquals(existingReservationResponse.dinerEmail(), reservationResponseResult.dinerEmail());
        assertEquals(existingReservationResponse.groupSize(), reservationResponseResult.groupSize());
    }

    @Test
    void createAutoAssignReservation_whenExistingConflictingReservationIsCancelled() throws Exception {
        Restaurant restaurantRequest = getRestaurant();
        Restaurant restaurant = restaurantRepository.save(restaurantRequest);

        Room roomPayload = getRoom();
        roomPayload.setRestaurant(restaurant);
        Room room = roomRepository.save(roomPayload);

        Reservation reservation = getReservation(restaurant, room);
        reservation.setReservationDate(LocalDate.of(2025, 12, 20));
        reservation.setReservationStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);

        AutoAssignReservationRequest autoAssignReservationRequest = getAutoAssignReservationRequest();

        String url = "/api/reservations/auto-assign";
        ResultActions result = performPost(url, autoAssignReservationRequest, HttpStatus.CREATED)
                .andExpect(jsonPath("$.roomId").exists())
                .andExpect(jsonPath("$.restaurantId").exists())
                .andExpect(jsonPath("$.roomName").exists())
                .andExpect(jsonPath("$.reservationDate").exists())
                .andExpect(jsonPath("$.reservationStartTime").exists())
                .andExpect(jsonPath("$.reservationEndTime").exists())
                .andExpect(jsonPath("$.reservationStatus").exists())
                .andExpect(jsonPath("$.dinerEmail").exists())
                .andExpect(jsonPath("$.groupSize").exists());

        String reservationResponse = result.andReturn().getResponse().getContentAsString();
        ReservationResponse reservationResponseResult = objectMapper.readValue(reservationResponse, ReservationResponse.class);
        Reservation existing = reservationRepository.findById(reservationResponseResult.id()).orElseThrow();
        ReservationResponse existingReservationResponse = reservationMapper.toResponse(existing);
        assertNotNull(reservationResponseResult);
        assertEquals(existingReservationResponse.roomId(), reservationResponseResult.roomId());
        assertEquals(existingReservationResponse.restaurantId(), reservationResponseResult.restaurantId());
        assertEquals(existingReservationResponse.roomName(), reservationResponseResult.roomName());
        assertEquals(existingReservationResponse.reservationDate(), reservationResponseResult.reservationDate());
        assertEquals(existingReservationResponse.reservationStartTime(), reservationResponseResult.reservationStartTime());
        assertEquals(existingReservationResponse.reservationEndTime(), reservationResponseResult.reservationEndTime());
        assertEquals(existingReservationResponse.reservationStatus(), reservationResponseResult.reservationStatus());
        assertEquals(existingReservationResponse.dinerEmail(), reservationResponseResult.dinerEmail());
        assertEquals(existingReservationResponse.groupSize(), reservationResponseResult.groupSize());
    }

    @Test
    void failedAutoAssignReservation_whenOverlappingReservationExistingEarlier() throws Exception {
        Restaurant restaurantRequest = getRestaurant();
        Restaurant restaurant = restaurantRepository.save(restaurantRequest);

        Room roomPayload = getRoom();
        roomPayload.setRestaurant(restaurant);
        Room room = roomRepository.save(roomPayload);

        Reservation reservation = getReservation(restaurant, room);
        reservation.setReservationDate(LocalDate.of(2025, 12, 20));
        reservationRepository.save(reservation);

        AutoAssignReservationRequest autoAssignReservationRequest = getAutoAssignReservationRequest();

        String url = "/api/reservations/auto-assign";
        ResultActions result = performPost(url, autoAssignReservationRequest, HttpStatus.CONFLICT);

        String reservationResponse = result.andReturn().getResponse().getContentAsString();
        assertEquals("{\"error\":\"No Room is available for Restaurant id: 1  Reservation date: 2025-12-20 Room Type: ROOFTOP\"}", reservationResponse);
    }

    @Test
    void listReservations() throws Exception {
        Restaurant restaurantRequest = getRestaurant();
        Restaurant restaurant = restaurantRepository.save(restaurantRequest);

        Room roomPayload = getRoom();
        roomPayload.setRestaurant(restaurant);
        Room room = roomRepository.save(roomPayload);

        Reservation reservation = getReservation(restaurant, room);
        reservation.setReservationDate(LocalDate.of(2025, 12, 20));
        reservationRepository.save(reservation);

        Reservation reservationNew = getReservation(restaurant, room);
        reservation.setReservationDate(LocalDate.of(2025, 12, 20));
        reservationRepository.save(reservationNew);

        AutoAssignReservationRequest autoAssignReservationRequest = getAutoAssignReservationRequest();

        String url = "/api/reservations?restaurantId=1";
        performGet(url)
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void cancelReservation() throws Exception {
        Restaurant restaurantRequest = getRestaurant();
        Restaurant restaurant = restaurantRepository.save(restaurantRequest);

        Room roomPayload = getRoom();
        roomPayload.setRestaurant(restaurant);
        Room room = roomRepository.save(roomPayload);

        Reservation reservation = getReservation(restaurant, room);
        reservation.setReservationDate(LocalDate.of(2025, 12, 20));
        reservationRepository.save(reservation);

        String url = "/api/reservations/cancel/" + reservation.getId();
        performDelete(url);

        Reservation existing = reservationRepository.findById(reservation.getId()).orElseThrow();
        ReservationResponse existingReservationResponse = reservationMapper.toResponse(existing);
        assertNotNull(existingReservationResponse);
        assertEquals(reservation.getRoom().getId(), existingReservationResponse.roomId());
        assertEquals(reservation.getRestaurant().getId(), existingReservationResponse.restaurantId());
        assertEquals(reservation.getReservationDate(), existingReservationResponse.reservationDate());
        assertEquals(reservation.getReservationStartTime(), existingReservationResponse.reservationStartTime());
        assertEquals(reservation.getReservationEndTime(), existingReservationResponse.reservationEndTime());
        assertEquals(ReservationStatus.CANCELLED, existingReservationResponse.reservationStatus());
        assertEquals(reservation.getDinerEmail(), existingReservationResponse.dinerEmail());
        assertEquals(reservation.getGroupSize(), existingReservationResponse.groupSize());

    }

}
