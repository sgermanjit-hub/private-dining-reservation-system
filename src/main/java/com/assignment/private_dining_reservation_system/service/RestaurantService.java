package com.assignment.private_dining_reservation_system.service;

import com.assignment.private_dining_reservation_system.entity.Restaurant;
import com.assignment.private_dining_reservation_system.entity.Room;
import com.assignment.private_dining_reservation_system.exception.EntityNotFoundException;
import com.assignment.private_dining_reservation_system.model.request.RestaurantRequest;
import com.assignment.private_dining_reservation_system.model.request.RoomRequest;
import com.assignment.private_dining_reservation_system.repository.RestaurantRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final RoomService roomService;

    public RestaurantService(RestaurantRepository restaurantRepository, RoomService roomService) {
        this.restaurantRepository = restaurantRepository;
        this.roomService = roomService;
    }


    public Restaurant createRestaurant(RestaurantRequest restaurantRequest) {
        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantName(restaurantRequest.restaurantName());
        restaurant.setEmail(restaurantRequest.email());
        restaurant.setAddress(restaurantRequest.address());
        restaurant.setLocalCurrencyCode(restaurantRequest.localCurrencyCode());
        restaurant.setContact(restaurantRequest.contact());
        return restaurantRepository.save(restaurant);
    }

    public Restaurant getById(Long restaurantId) {
        return restaurantRepository.findById(restaurantId).orElseThrow(() -> new EntityNotFoundException("Restaurant Not Found"));
    }

    public Restaurant updateRestaurant(Long restaurantId, RestaurantRequest restaurantRequest) {
        Restaurant restaurant = getById(restaurantId);
        restaurant.setRestaurantName(restaurantRequest.restaurantName());
        restaurant.setEmail(restaurantRequest.email());
        restaurant.setAddress(restaurantRequest.address());
        restaurant.setLocalCurrencyCode(restaurantRequest.localCurrencyCode());
        restaurant.setContact(restaurantRequest.contact());
        return restaurantRepository.save(restaurant);
    }

    public Room createRoom(Long restaurantId, RoomRequest roomRequest) {
        Restaurant restaurant = getById(restaurantId);
        return roomService.createRoom(restaurant, roomRequest);
    }

    public Room updateRoom(Long restaurantId, Long roomId, RoomRequest roomRequest) {
        Restaurant restaurant = getById(restaurantId);
        return roomService.updateRoom(restaurant, roomId, roomRequest);
    }

    public List<Room> getRoomsForRestaurant(Long restaurantId) {
        getById(restaurantId);
        return roomService.getRoomsForRestaurant(restaurantId);
    }

}
