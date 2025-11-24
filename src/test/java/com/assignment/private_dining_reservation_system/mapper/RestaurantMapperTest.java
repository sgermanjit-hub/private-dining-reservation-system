package com.assignment.private_dining_reservation_system.mapper;

import com.assignment.private_dining_reservation_system.BaseTest;
import com.assignment.private_dining_reservation_system.entity.Restaurant;
import com.assignment.private_dining_reservation_system.model.response.RestaurantResponse;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RestaurantMapperTest extends BaseTest {
    @InjectMocks
    RestaurantMapper restaurantMapper;

    @Test
    void toResponseTest() {
        Restaurant restaurant = getRestaurant();
        RestaurantResponse result = restaurantMapper.toResponse(restaurant);
        assertNotNull(result);
        assertEquals(restaurant.getAddress(), result.address());
        assertEquals(restaurant.getEmail(), result.email());
        assertEquals(restaurant.getId(), result.id());
        assertEquals(restaurant.getContact(), result.contact());
        assertEquals(restaurant.getLocalCurrencyCode(), result.localCurrencyCode());
    }
}