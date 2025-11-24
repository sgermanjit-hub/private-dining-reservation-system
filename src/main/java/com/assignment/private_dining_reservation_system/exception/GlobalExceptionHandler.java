package com.assignment.private_dining_reservation_system.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ReservationValidationFailureException.class)
    public ResponseEntity<Map<String, String>> handleValidationFailure(ReservationValidationFailureException reservationValidationFailureException) {
        log.warn("Reservation Validation Failure: {}", reservationValidationFailureException.getMessage());
        return ResponseEntity.badRequest().body(Map.of("error", reservationValidationFailureException.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException methodArgumentNotValidException) {
        Map<String, String> failureErrors = methodArgumentNotValidException
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (a, b) -> b
                ));
        log.warn("Validation errors: {}", failureErrors);
        return ResponseEntity.badRequest().body(Map.of("error", "Validation Failed", "Details", failureErrors));
    }

    @ExceptionHandler(ReservationFailedException.class)
    public ResponseEntity<Map<String, String>> handleConflict(ReservationFailedException reservationFailedException) {
        log.warn("Reservation Failed: {}", reservationFailedException.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", reservationFailedException.getMessage()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleEntityNotFound(EntityNotFoundException entityNotFoundException) {
        log.warn("Entity Not Found: {}", entityNotFoundException.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", entityNotFoundException.getMessage()));
    }

    @ExceptionHandler(RoomNotAvailableException.class)
    public ResponseEntity<Map<String, String>> handleRoomNotAvailable(RoomNotAvailableException roomNotAvailableException) {
        log.warn("Room Not Available Exception: {}", roomNotAvailableException.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", roomNotAvailableException.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception exception) {
        log.error("Server Error: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", exception.getMessage()));
    }

}
