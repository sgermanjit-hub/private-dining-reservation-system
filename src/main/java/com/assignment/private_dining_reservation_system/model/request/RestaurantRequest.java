package com.assignment.private_dining_reservation_system.model.request;

import com.assignment.private_dining_reservation_system.entity.RestaurantCurrency;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RestaurantRequest(
        @NotBlank
        @Schema(type = "string",
                example = "Test ABC",
                description = "Name of the restaurant")
        String restaurantName,

        @NotBlank
        @Schema(type = "string",
                example = "ABC Street, Hill Road",
                description = "Address of the restaurant")
        String address,

        @NotBlank
        @Schema(type = "string",
                example = "+x1xxxxxxxxx",
                description = "Contact of the restaurant")
        String contact,

        @NotBlank
        @Schema(type = "string",
                example = "restaurant@gmail.com",
                description = "Email of the restaurant")
        String email,

        @NotNull
        @Schema(type = "string",
                example = "AUD",
                description = "Restaurant Local Currency",
                allowableValues = {"AUD"})
        RestaurantCurrency localCurrencyCode
) {
}
