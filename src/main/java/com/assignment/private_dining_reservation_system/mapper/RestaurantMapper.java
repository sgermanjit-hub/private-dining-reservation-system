package com.assignment.private_dining_reservation_system.mapper;

import com.assignment.private_dining_reservation_system.entity.Restaurant;
import com.assignment.private_dining_reservation_system.model.response.RestaurantResponse;
import org.springframework.stereotype.Component;

@Component
public class RestaurantMapper {

    public RestaurantResponse toResponse(Restaurant restaurant) {
        return new RestaurantResponse(
                restaurant.getId(),
                restaurant.getRestaurantName(),
                restaurant.getAddress(),
                restaurant.getContact(),
                restaurant.getEmail(),
                restaurant.getLocalCurrencyCode()
        );
    }
}
