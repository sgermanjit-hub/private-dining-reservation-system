package com.assignment.private_dining_reservation_system.exception;

public class ReservationValidationFailureException extends RuntimeException {
    public ReservationValidationFailureException(String message) {
        super(message);
    }
}
