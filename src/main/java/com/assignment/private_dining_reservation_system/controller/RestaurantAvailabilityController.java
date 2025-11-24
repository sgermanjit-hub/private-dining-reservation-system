package com.assignment.private_dining_reservation_system.controller;

import com.assignment.private_dining_reservation_system.entity.Room;
import com.assignment.private_dining_reservation_system.mapper.RoomMapper;
import com.assignment.private_dining_reservation_system.model.request.ReservationTimeFrame;
import com.assignment.private_dining_reservation_system.model.response.RoomResponse;
import com.assignment.private_dining_reservation_system.service.RestaurantAvailabilityService;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/restaurant/{restaurantId}")
public class RestaurantAvailabilityController {

    private final RestaurantAvailabilityService restaurantAvailabilityService;
    private final RoomMapper roomMapper;

    public RestaurantAvailabilityController(RestaurantAvailabilityService restaurantAvailabilityService, RoomMapper roomMapper) {
        this.restaurantAvailabilityService = restaurantAvailabilityService;
        this.roomMapper = roomMapper;
    }

    /**
     * Find Available Rooms
     *
     * @param restaurantId restaurantId
     * @param date         reservation date
     * @param startTime    reservation start time
     * @param endTime      reservation end time
     * @return List<RoomResponse>
     */
    @GetMapping("/available-rooms")
    public ResponseEntity<List<RoomResponse>> availableRooms(@PathVariable("restaurantId") Long restaurantId,
                                                             @RequestParam(name = "date", required = false) LocalDate date,
                                                             @RequestParam(name = "startTime", required = false) @Schema(type = "string", example = "18:00") @JsonFormat(pattern = "HH:mm") LocalTime startTime,
                                                             @RequestParam(name = "endTime", required = false) @Schema(type = "string", example = "21:00") @JsonFormat(pattern = "HH:mm") LocalTime endTime) {
        List<Room> rooms = restaurantAvailabilityService.findAvailableRoomsByRestaurantId(restaurantId, defaultReservationTimeFrame(date, startTime, endTime));
        return ResponseEntity.ok(rooms.stream().map(roomMapper::toResponse).toList());
    }

    /**
     * Setting up default values for current day and current time to end of day
     *
     */
    private ReservationTimeFrame defaultReservationTimeFrame(LocalDate date, LocalTime startTime, LocalTime endTime) {
        LocalDate d = Optional.ofNullable(date).orElse(LocalDate.now());
        LocalTime st = Optional.ofNullable(startTime).orElse(LocalTime.of(0, 0));
        if (d.isEqual(LocalDate.now())) {
            st = LocalTime.of(LocalTime.now().getHour(), LocalTime.now().getMinute());
        }
        LocalTime et = Optional.ofNullable(endTime).orElse(LocalTime.of(23, 59));
        return new ReservationTimeFrame(d, st, et);

    }
}
