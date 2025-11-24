package com.assignment.private_dining_reservation_system.controller;

import com.assignment.private_dining_reservation_system.entity.Restaurant;
import com.assignment.private_dining_reservation_system.entity.Room;
import com.assignment.private_dining_reservation_system.mapper.RestaurantMapper;
import com.assignment.private_dining_reservation_system.mapper.RoomMapper;
import com.assignment.private_dining_reservation_system.model.request.RestaurantRequest;
import com.assignment.private_dining_reservation_system.model.request.RoomRequest;
import com.assignment.private_dining_reservation_system.model.response.RestaurantResponse;
import com.assignment.private_dining_reservation_system.model.response.RoomResponse;
import com.assignment.private_dining_reservation_system.repository.RestaurantRepository;
import com.assignment.private_dining_reservation_system.repository.RoomRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class RestaurantControllerIT extends BaseIntegrationTest {

    @Autowired
    RestaurantRepository restaurantRepository;

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    RestaurantMapper restaurantMapper;

    @Autowired
    RoomMapper roomMapper;

    @Test
    void createRestaurant_verifyRestaurant() throws Exception {
        RestaurantRequest restaurantRequest = getRestaurantRequest();

        ResultActions result = performPost("/api/restaurants", restaurantRequest, HttpStatus.CREATED)
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.address").exists())
                .andExpect(jsonPath("$.contact").exists())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.localCurrencyCode").exists());

        String restaurantResponse = result.andReturn().getResponse().getContentAsString();
        RestaurantResponse restaurantResponseDTO = objectMapper.readValue(restaurantResponse, RestaurantResponse.class);

        Restaurant restaurant = restaurantRepository.findById(restaurantResponseDTO.id()).orElseThrow();
        RestaurantResponse restaurantResponseResult = restaurantMapper.toResponse(restaurant);

        assertNotNull(restaurantResponseResult);
        assertEquals(restaurant.getId(), restaurantResponseResult.id());
        assertEquals(restaurantRequest.restaurantName(), restaurantResponseResult.name());
        assertEquals(restaurantRequest.address(), restaurantResponseResult.address());
        assertEquals(restaurantRequest.contact(), restaurantResponseResult.contact());
        assertEquals(restaurantRequest.localCurrencyCode(), restaurantResponseResult.localCurrencyCode());
        assertEquals(restaurantRequest.email(), restaurantResponseResult.email());
    }

    @Test
    void getRestaurant_whenAlreadyCreated() throws Exception {
        Restaurant restaurantRequest = getRestaurant();
        Restaurant restaurant = restaurantRepository.save(restaurantRequest);
        Long id = restaurant.getId();
        ResultActions result = performGet("/api/restaurants/" + id)
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.address").exists())
                .andExpect(jsonPath("$.contact").exists())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.localCurrencyCode").exists());

        String restaurantResponse = result.andReturn().getResponse().getContentAsString();
        RestaurantResponse restaurantResponseDTO = objectMapper.readValue(restaurantResponse, RestaurantResponse.class);

        assertNotNull(restaurantResponseDTO);
        assertEquals(restaurantRequest.getRestaurantName(), restaurantResponseDTO.name());
        assertEquals(restaurantRequest.getAddress(), restaurantResponseDTO.address());
        assertEquals(restaurantRequest.getContact(), restaurantResponseDTO.contact());
        assertEquals(restaurantRequest.getLocalCurrencyCode(), restaurantResponseDTO.localCurrencyCode());
        assertEquals(restaurantRequest.getEmail(), restaurantResponseDTO.email());
    }

    @Test
    void updateRestaurant_whenAlreadyCreated() throws Exception {
        Restaurant restaurantPayload = getRestaurant();
        Restaurant restaurant = restaurantRepository.save(restaurantPayload);
        Long id = restaurant.getId();

        RestaurantRequest restaurantRequest = getRestaurantRequest();

        ResultActions result = performPut("/api/restaurants/" + id, restaurantRequest)
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.address").exists())
                .andExpect(jsonPath("$.contact").exists())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.localCurrencyCode").exists());

        String restaurantResponse = result.andReturn().getResponse().getContentAsString();
        RestaurantResponse restaurantResponseDTO = objectMapper.readValue(restaurantResponse, RestaurantResponse.class);

        assertNotNull(restaurantResponseDTO);
        assertEquals(restaurantRequest.restaurantName(), restaurantResponseDTO.name());
        assertEquals(restaurantRequest.address(), restaurantResponseDTO.address());
        assertEquals(restaurantRequest.contact(), restaurantResponseDTO.contact());
        assertEquals(restaurantRequest.localCurrencyCode(), restaurantResponseDTO.localCurrencyCode());
        assertEquals(restaurantRequest.email(), restaurantResponseDTO.email());
    }

    @Test
    void createRoom_whenRestaurantAlreadyCreated() throws Exception {
        Restaurant restaurantRequest = getRestaurant();
        Restaurant restaurant = restaurantRepository.save(restaurantRequest);
        Long id = restaurant.getId();

        RoomRequest roomRequest = getRoomRequest();
        String url = "/api/restaurants/" + id + "/room";
        ResultActions result = performPost(url, roomRequest, HttpStatus.CREATED)
                .andExpect(jsonPath("$.roomId").exists())
                .andExpect(jsonPath("$.restaurantId").exists())
                .andExpect(jsonPath("$.roomName").exists())
                .andExpect(jsonPath("$.roomType").exists())
                .andExpect(jsonPath("$.minCapacity").exists())
                .andExpect(jsonPath("$.maxCapacity").exists())
                .andExpect(jsonPath("$.minSpend").exists());

        String roomResponse = result.andReturn().getResponse().getContentAsString();
        RoomResponse roomResponseDTO = objectMapper.readValue(roomResponse, RoomResponse.class);
        Room roomResponseResult = roomRepository.findById(roomResponseDTO.roomId()).orElseThrow();
        RoomResponse roomResponseVerify = roomMapper.toResponse(roomResponseResult);

        assertNotNull(roomResponseDTO);
        assertEquals(roomResponseVerify.roomId(), roomResponseDTO.roomId());
        assertEquals(roomResponseVerify.restaurantId(), roomResponseDTO.restaurantId());
        assertEquals(roomResponseVerify.roomName(), roomResponseDTO.roomName());
        assertEquals(roomResponseVerify.roomType(), roomResponseDTO.roomType());
        assertEquals(roomResponseVerify.minCapacity(), roomResponseDTO.minCapacity());
        assertEquals(roomResponseVerify.maxCapacity(), roomResponseDTO.maxCapacity());
        assertEquals(roomResponseVerify.minSpend(), roomResponseDTO.minSpend());
    }

    @Test
    void getRoom_whenRestaurantAndRoomAlreadyCreated() throws Exception {
        Restaurant restaurantRequest = getRestaurant();
        Restaurant restaurant = restaurantRepository.save(restaurantRequest);
        Long id = restaurant.getId();

        Room roomPayload = getRoom();
        roomPayload.setRestaurant(restaurant);
        Room room = roomRepository.save(roomPayload);

        String url = "/api/restaurants/" + id + "/rooms";
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
    void updateRoom_whenRestaurantAndRoomAlreadyCreated() throws Exception {
        Restaurant restaurantRequest = getRestaurant();
        Restaurant restaurant = restaurantRepository.save(restaurantRequest);
        Long id = restaurant.getId();

        Room roomPayload = getRoom();
        roomPayload.setRestaurant(restaurant);
        Room room = roomRepository.save(roomPayload);
        Long roomId = room.getId();

        RoomRequest roomRequest = getRoomRequest();
        String url = "/api/restaurants/" + id + "/room/" + roomId;
        ResultActions result = performPut(url, roomRequest)
                .andExpect(jsonPath("$.roomId").exists())
                .andExpect(jsonPath("$.restaurantId").exists())
                .andExpect(jsonPath("$.roomName").exists())
                .andExpect(jsonPath("$.roomType").exists())
                .andExpect(jsonPath("$.minCapacity").exists())
                .andExpect(jsonPath("$.maxCapacity").exists())
                .andExpect(jsonPath("$.minSpend").exists());

        String roomResponse = result.andReturn().getResponse().getContentAsString();
        RoomResponse roomResponseDTO = objectMapper.readValue(roomResponse, RoomResponse.class);

        assertNotNull(roomResponseDTO);
        assertEquals(roomId, roomResponseDTO.roomId());
        assertEquals(id, roomResponseDTO.restaurantId());
        assertEquals(roomRequest.roomName(), roomResponseDTO.roomName());
        assertEquals(roomRequest.roomType(), roomResponseDTO.roomType());
        assertEquals(roomRequest.minCapacity(), roomResponseDTO.minCapacity());
        assertEquals(roomRequest.maxCapacity(), roomResponseDTO.maxCapacity());
        assertEquals(roomRequest.minSpendInCents(), roomResponseDTO.minSpend());
    }
}
