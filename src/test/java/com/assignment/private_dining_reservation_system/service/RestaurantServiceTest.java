package com.assignment.private_dining_reservation_system.service;

import com.assignment.private_dining_reservation_system.BaseTest;
import com.assignment.private_dining_reservation_system.entity.Restaurant;
import com.assignment.private_dining_reservation_system.entity.RestaurantCurrency;
import com.assignment.private_dining_reservation_system.entity.Room;
import com.assignment.private_dining_reservation_system.exception.EntityNotFoundException;
import com.assignment.private_dining_reservation_system.model.request.RestaurantRequest;
import com.assignment.private_dining_reservation_system.model.request.RoomRequest;
import com.assignment.private_dining_reservation_system.repository.RestaurantRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

class RestaurantServiceTest extends BaseTest {

    @Mock
    RestaurantRepository restaurantRepository;
    @Mock
    RoomService roomService;
    @InjectMocks
    RestaurantService restaurantService;

    @Test
    void createRestaurant() {
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(getRestaurant());
        RestaurantRequest restaurantRequest = getRestaurantRequest();
        Restaurant restaurant = restaurantService.createRestaurant(restaurantRequest);
        assertEquals(restaurant.getRestaurantName(), restaurantRequest.restaurantName());
        assertEquals(restaurant.getAddress(), restaurantRequest.address());
        assertEquals(restaurant.getContact(), restaurantRequest.contact());
        assertEquals(restaurant.getLocalCurrencyCode(), restaurantRequest.localCurrencyCode());
        assertEquals(restaurant.getEmail(), restaurantRequest.email());
    }

    @Test
    void getById() {
        Restaurant restaurant = getRestaurant();
        when(restaurantRepository.findById(anyLong())).thenReturn(Optional.of(restaurant));
        Restaurant restaurantResult = restaurantService.getById(1L);
        assertEquals(restaurant.getRestaurantName(), restaurantResult.getRestaurantName());
        assertEquals(restaurant.getAddress(), restaurantResult.getAddress());
        assertEquals(restaurant.getContact(), restaurantResult.getContact());
        assertEquals(restaurant.getLocalCurrencyCode(), restaurantResult.getLocalCurrencyCode());
        assertEquals(restaurant.getEmail(), restaurantResult.getEmail());
    }

    @Test
    void getById_ExceptionTest() {
        when(restaurantRepository.findById(anyLong())).thenThrow(new EntityNotFoundException("Entity not found"));
        assertThrows(EntityNotFoundException.class, () -> restaurantService.getById(1L));
    }

    @Test
    void updateRestaurant() {
        Restaurant restaurantUpdated = new Restaurant();
        restaurantUpdated.setRestaurantName("Hotel Name");
        restaurantUpdated.setAddress("New Address");
        restaurantUpdated.setContact("+2xxxxxxx");
        restaurantUpdated.setEmail("res2@gmail.com");
        restaurantUpdated.setLocalCurrencyCode(RestaurantCurrency.AUD);
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurantUpdated);
        when(restaurantRepository.findById(anyLong())).thenReturn(Optional.of(getRestaurant()));

        RestaurantRequest restaurantRequest = getRestaurantRequest();
        Restaurant restaurantResult = restaurantService.updateRestaurant(1L, restaurantRequest);
        assertEquals(restaurantUpdated.getRestaurantName(), restaurantResult.getRestaurantName());
        assertEquals(restaurantUpdated.getAddress(), restaurantResult.getAddress());
        assertEquals(restaurantUpdated.getContact(), restaurantResult.getContact());
        assertEquals(restaurantUpdated.getLocalCurrencyCode(), restaurantResult.getLocalCurrencyCode());
        assertEquals(restaurantUpdated.getEmail(), restaurantResult.getEmail());
    }

    @Test
    void updateRestaurant_ExceptionTest() {
        Restaurant restaurantUpdated = new Restaurant();
        restaurantUpdated.setRestaurantName("Hotel Name");
        restaurantUpdated.setAddress("New Address");
        restaurantUpdated.setContact("+2xxxxxxx");
        restaurantUpdated.setEmail("res2@gmail.com");
        restaurantUpdated.setLocalCurrencyCode(RestaurantCurrency.AUD);
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurantUpdated);
        when(restaurantRepository.findById(anyLong())).thenReturn(Optional.of(getRestaurant()));

        RestaurantRequest restaurantRequest = getRestaurantRequest();
        Restaurant restaurantResult = restaurantService.updateRestaurant(1L, restaurantRequest);
        assertEquals(restaurantUpdated.getRestaurantName(), restaurantResult.getRestaurantName());
        assertEquals(restaurantUpdated.getAddress(), restaurantResult.getAddress());
        assertEquals(restaurantUpdated.getContact(), restaurantResult.getContact());
        assertEquals(restaurantUpdated.getLocalCurrencyCode(), restaurantResult.getLocalCurrencyCode());
        assertEquals(restaurantUpdated.getEmail(), restaurantResult.getEmail());
    }

    @Test
    void createRoom() {
        RoomRequest roomRequest = getRoomRequest();
        Restaurant restaurant = getRestaurant();
        Room room = getRoom();
        when(restaurantRepository.findById(anyLong())).thenReturn(Optional.of(restaurant));
        when(roomService.createRoom(restaurant, roomRequest)).thenReturn(room);
        Room roomResult = restaurantService.createRoom(1L, roomRequest);
        assertEquals(room.getRestaurant(), roomResult.getRestaurant());
        assertEquals(room.getRoomName(), roomResult.getRoomName());
        assertEquals(room.getMinCapacity(), roomResult.getMinCapacity());
        assertEquals(room.getMaxCapacity(), roomResult.getMaxCapacity());
        assertEquals(room.getRoomType(), roomResult.getRoomType());
    }

    @Test
    void updateRoom() {
        RoomRequest roomRequest = getRoomRequest();
        Restaurant restaurant = getRestaurant();
        Room room = getRoom();
        when(restaurantRepository.findById(anyLong())).thenReturn(Optional.of(restaurant));
        when(roomService.updateRoom(restaurant, 1L, roomRequest)).thenReturn(room);
        Room roomResult = restaurantService.updateRoom(1L, 1L, roomRequest);
        assertEquals(room.getRestaurant(), roomResult.getRestaurant());
        assertEquals(room.getRoomName(), roomResult.getRoomName());
        assertEquals(room.getMinCapacity(), roomResult.getMinCapacity());
        assertEquals(room.getMaxCapacity(), roomResult.getMaxCapacity());
        assertEquals(room.getRoomType(), roomResult.getRoomType());
    }

    @Test
    void getRoomsForRestaurant() {
        Restaurant restaurant = getRestaurant();
        Room room = getRoom();
        when(restaurantRepository.findById(anyLong())).thenReturn(Optional.of(restaurant));
        when(roomService.getRoomsForRestaurant(1L)).thenReturn(List.of(room));
        List<Room> roomList = restaurantService.getRoomsForRestaurant(1L);
        assertNotNull(roomList);
        assertEquals(1, roomList.size());
        assertEquals(room.getRestaurant(), roomList.getFirst().getRestaurant());
        assertEquals(room.getRoomName(), roomList.getFirst().getRoomName());
        assertEquals(room.getMinCapacity(), roomList.getFirst().getMinCapacity());
        assertEquals(room.getMaxCapacity(), roomList.getFirst().getMaxCapacity());
        assertEquals(room.getRoomType(), roomList.getFirst().getRoomType());
    }
}