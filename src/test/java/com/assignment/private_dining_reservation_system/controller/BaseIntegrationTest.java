package com.assignment.private_dining_reservation_system.controller;

import com.assignment.private_dining_reservation_system.TestcontainersConfiguration;
import com.assignment.private_dining_reservation_system.entity.*;
import com.assignment.private_dining_reservation_system.model.request.AutoAssignReservationRequest;
import com.assignment.private_dining_reservation_system.model.request.ReservationRequest;
import com.assignment.private_dining_reservation_system.model.request.RestaurantRequest;
import com.assignment.private_dining_reservation_system.model.request.RoomRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
public abstract class BaseIntegrationTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;


    RestaurantRequest getRestaurantRequest() {
        return new RestaurantRequest(
                "Restaurant One",
                "Restaurant One Addres",
                "+1xxxxxxxxxx",
                "diner@gmail.com",
                RestaurantCurrency.AUD
        );
    }

    RoomRequest getRoomRequest() {
        return new RoomRequest(
                "Room",
                10,
                20,
                RoomType.ROOFTOP,
                new BigDecimal("1000.00"),
                LocalTime.of(15,30),
                LocalTime.of(23,30),
                null);
    }

    Restaurant getRestaurant() {
        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantName("Restaurant");
        restaurant.setLocalCurrencyCode(RestaurantCurrency.AUD);
        restaurant.setAddress("Restaurant");
        restaurant.setContact("+1xxxxxxxxxx");
        restaurant.setEmail("diner@gmail.com");
        return restaurant;
    }

    Reservation getReservation(Restaurant restaurant, Room room) {
        Reservation reservation = new Reservation();
        reservation.setRestaurant(restaurant);
        reservation.setRoom(room);
        reservation.setReservationStartTime(LocalTime.parse("15:30"));
        reservation.setReservationEndTime(LocalTime.parse("18:30"));
        reservation.setReservationDate(LocalDate.of(2027, 11, 25));
        reservation.setReservationStatus(ReservationStatus.CONFIRMED);
        reservation.setGroupSize(15);
        reservation.setDinerEmail("diner@gmail.com");
        return reservation;
    }

    ReservationRequest getReservationRequest() {
        return new ReservationRequest(
                1L,
                1L,
                LocalTime.parse("15:30"),
                LocalTime.parse("18:30"),
                LocalDate.of(2025, 12, 20),
                15,
                "diner@gmail.com");
    }

    AutoAssignReservationRequest getAutoAssignReservationRequest() {
        return new AutoAssignReservationRequest(
                1L,
                RoomType.ROOFTOP,
                LocalTime.parse("15:30"),
                LocalTime.parse("18:30"),
                LocalDate.of(2025, 12, 20),
                15,
                "diner@gmail.com");
    }

    Room getRoom() {
        Room room = new Room();
        room.setRoomName("Room");
        room.setMinCapacity(10);
        room.setMaxCapacity(20);
        room.setRoomType(RoomType.ROOFTOP);
        room.setMinSpendInCents(new BigDecimal("1000.00"));
        room.setRoomMetaData(getRoomMetaData(room));
        return room;
    }

    RoomMetaData getRoomMetaData(Room room){
        RoomMetaData roomMetaData = new RoomMetaData();
        roomMetaData.setRoomClosingTime(LocalTime.of(23,59));
        roomMetaData.setRoomOpeningTime(LocalTime.of(00, 30));
        roomMetaData.setOpenDays(EnumSet.allOf(DayOfWeek.class));
        roomMetaData.setRoom(room);
        return roomMetaData;
    }

    Room getSecondRoom() {
        Room room = new Room();
        room.setRoomName("Room");
        room.setMinCapacity(10);
        room.setMaxCapacity(20);
        room.setRoomType(RoomType.ROOFTOP);
        room.setMinSpendInCents(new BigDecimal("1000.00"));
        room.setRoomMetaData(getRoomMetaData(room));
        return room;
    }

    String getJsonFormat(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    ResultActions performPost(String url, Object request, HttpStatus httpStatus) throws Exception {
        String requestJson = getJsonFormat(request);
        return mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().is(httpStatus.value()));
    }

    ResultActions performGet(String url) throws Exception {
        return mockMvc.perform(get(url))
                .andExpect(status().is(HttpStatus.OK.value()));
    }

    ResultActions performDelete(String url) throws Exception {
        return mockMvc.perform(delete(url))
                .andExpect(status().is(HttpStatus.NO_CONTENT.value()));
    }

    ResultActions performPut(String url, Object request) throws Exception {
        String requestJson = getJsonFormat(request);
        return mockMvc.perform(put(url).contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().is(HttpStatus.OK.value()));
    }
}
