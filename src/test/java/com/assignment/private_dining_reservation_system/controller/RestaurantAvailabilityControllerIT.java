package com.assignment.private_dining_reservation_system.controller;

import com.assignment.private_dining_reservation_system.entity.Reservation;
import com.assignment.private_dining_reservation_system.entity.Restaurant;
import com.assignment.private_dining_reservation_system.entity.Room;
import com.assignment.private_dining_reservation_system.model.response.RoomResponse;
import com.assignment.private_dining_reservation_system.repository.ReservationRepository;
import com.assignment.private_dining_reservation_system.repository.RestaurantRepository;
import com.assignment.private_dining_reservation_system.repository.RoomRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import java.lang.reflect.Type;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class RestaurantAvailabilityControllerIT extends BaseIntegrationTest {
    @Autowired
    RestaurantRepository restaurantRepository;
    @Autowired
    RoomRepository roomRepository;
    @Autowired
    ReservationRepository reservationRepository;

    @Test
    void getAvailableRooms_whenNoReservationExists() throws Exception {
        Restaurant restaurantRequest = getRestaurant();
        Restaurant restaurant = restaurantRepository.save(restaurantRequest);
        Long id = restaurant.getId();
        Room roomPayload = getRoom();
        roomPayload.setRestaurant(restaurant);
        Room room = roomRepository.save(roomPayload);
        String url = "/api/restaurant/" + id + "/available-rooms";
        ResultActions result = performGet(url)
                .andExpect(jsonPath("$[0].roomId").exists())
                .andExpect(jsonPath("$[0].restaurantId").exists())
                .andExpect(jsonPath("$[0].roomName").exists())
                .andExpect(jsonPath("$[0].roomType").exists())
                .andExpect(jsonPath("$[0].minCapacity").exists())
                .andExpect(jsonPath("$[0].maxCapacity").exists())
                .andExpect(jsonPath("$[0].minSpend").exists());

        String roomResponse = result.andReturn().getResponse().getContentAsString();
        List<RoomResponse> roomResponseDTO = objectMapper.readValue(roomResponse, new TypeReference<List<RoomResponse>>() {
            @Override
            public Type getType() {
                return super.getType();
            }
        });

        assertNotNull(roomResponseDTO);
        assertEquals(room.getId(), roomResponseDTO.getFirst().roomId());
        assertEquals(room.getRestaurant().getId(), roomResponseDTO.getFirst().restaurantId());
        assertEquals(room.getRoomName(), roomResponseDTO.getFirst().roomName());
        assertEquals(room.getRoomType(), roomResponseDTO.getFirst().roomType());
        assertEquals(room.getMinCapacity(), roomResponseDTO.getFirst().minCapacity());
        assertEquals(room.getMaxCapacity(), roomResponseDTO.getFirst().maxCapacity());
        assertEquals(room.getMinSpendInCents(), roomResponseDTO.getFirst().minSpend());
    }

    @Test
    void getAvailableRooms_whenOneRoomReservationExistsAndOneEmpty() throws Exception {
        Restaurant restaurantRequest = getRestaurant();
        Restaurant restaurant = restaurantRepository.save(restaurantRequest);
        Long id = restaurant.getId();
        Room roomPayload = getRoom();
        roomPayload.setRestaurant(restaurant);
        Room room = roomRepository.save(roomPayload);
        //Inserting room twice for 2nd room entry
        Room roomPayloadSecond = getSecondRoom();
        roomPayloadSecond.setRestaurant(restaurant);
        Room room2 = roomRepository.save(roomPayloadSecond);
        Reservation reservation = getReservation(restaurant, room);
        reservationRepository.save(reservation);
        String url = "/api/restaurant/" + id + "/available-rooms?date=2027-11-25&startTime=15:30&endTime=18:30";
        ResultActions result = performGet(url)
                .andExpect(jsonPath("$[0].roomId").exists())
                .andExpect(jsonPath("$[0].restaurantId").exists())
                .andExpect(jsonPath("$[0].roomName").exists())
                .andExpect(jsonPath("$[0].roomType").exists())
                .andExpect(jsonPath("$[0].minCapacity").exists())
                .andExpect(jsonPath("$[0].maxCapacity").exists())
                .andExpect(jsonPath("$[0].minSpend").exists());

        String roomResponse = result.andReturn().getResponse().getContentAsString();
        List<RoomResponse> roomResponseDTO = objectMapper.readValue(roomResponse, new TypeReference<List<RoomResponse>>() {
            @Override
            public Type getType() {
                return super.getType();
            }
        });

        assertNotNull(roomResponseDTO);
        assertEquals(room2.getId(), roomResponseDTO.getFirst().roomId());
        assertEquals(room2.getRestaurant().getId(), roomResponseDTO.getFirst().restaurantId());
        assertEquals(room2.getRoomName(), roomResponseDTO.getFirst().roomName());
        assertEquals(room2.getRoomType(), roomResponseDTO.getFirst().roomType());
        assertEquals(room2.getMinCapacity(), roomResponseDTO.getFirst().minCapacity());
        assertEquals(room2.getMaxCapacity(), roomResponseDTO.getFirst().maxCapacity());
        assertEquals(room2.getMinSpendInCents(), roomResponseDTO.getFirst().minSpend());
    }

    @Test
    void getAvailableRooms_whenNoRoomAvailable() throws Exception {
        Restaurant restaurantRequest = getRestaurant();
        Restaurant restaurant = restaurantRepository.save(restaurantRequest);
        Long id = restaurant.getId();
        Room roomPayload = getRoom();
        roomPayload.setRestaurant(restaurant);
        Room room = roomRepository.save(roomPayload);
        Reservation reservation = getReservation(restaurant, room);
        reservationRepository.save(reservation);
        String url = "/api/restaurant/" + id + "/available-rooms?date=2027-11-25&startTime=15:30&endTime=18:30";
        ResultActions result = performGet(url)
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
