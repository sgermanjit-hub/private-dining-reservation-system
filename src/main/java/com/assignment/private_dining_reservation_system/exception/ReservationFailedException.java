package com.assignment.private_dining_reservation_system.exception;

public class ReservationFailedException extends RuntimeException {
    public ReservationFailedException(String message) {
        super(message);
    }
}
