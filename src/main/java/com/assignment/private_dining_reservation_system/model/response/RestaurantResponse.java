package com.assignment.private_dining_reservation_system.model.response;

import com.assignment.private_dining_reservation_system.entity.RestaurantCurrency;

public record RestaurantResponse(
        Long id,
        String name,
        String address,
        String contact,
        String email,
        RestaurantCurrency localCurrencyCode
) {

}
