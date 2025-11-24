package com.assignment.private_dining_reservation_system.controller;

import com.assignment.private_dining_reservation_system.entity.Room;
import com.assignment.private_dining_reservation_system.mapper.RestaurantMapper;
import com.assignment.private_dining_reservation_system.mapper.RoomMapper;
import com.assignment.private_dining_reservation_system.model.request.RestaurantRequest;
import com.assignment.private_dining_reservation_system.model.request.RoomRequest;
import com.assignment.private_dining_reservation_system.model.response.RestaurantResponse;
import com.assignment.private_dining_reservation_system.model.response.RoomResponse;
import com.assignment.private_dining_reservation_system.service.RestaurantService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final RestaurantMapper restaurantMapper;
    private final RoomMapper roomMapper;

    public RestaurantController(RestaurantService restaurantService, RestaurantMapper restaurantMapper, RoomMapper roomMapper) {
        this.restaurantService = restaurantService;
        this.restaurantMapper = restaurantMapper;
        this.roomMapper = roomMapper;
    }


    @PostMapping
    public ResponseEntity<RestaurantResponse> createRestaurant(@Valid @RequestBody RestaurantRequest restaurantRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(restaurantMapper.toResponse(restaurantService.createRestaurant(restaurantRequest)));
    }


    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponse> getRestaurant(@PathVariable Long id) {
        return ResponseEntity.ok(restaurantMapper.toResponse(restaurantService.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestaurantResponse> updateRestaurant(@PathVariable Long id, @Valid @RequestBody RestaurantRequest restaurantRequest) {
        return ResponseEntity.ok(restaurantMapper.toResponse(restaurantService.updateRestaurant(id, restaurantRequest)));
    }

    @PostMapping("{id}/room")
    public ResponseEntity<RoomResponse> createRoom(@PathVariable Long id, @Valid @RequestBody RoomRequest roomRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(roomMapper.toResponse(restaurantService.createRoom(id, roomRequest)));
    }

    @PutMapping("/{id}/room/{roomId}")
    public ResponseEntity<RoomResponse> updateRoom(@PathVariable Long id, @PathVariable Long roomId, @Valid @RequestBody RoomRequest roomRequest) {
        return ResponseEntity.ok(roomMapper.toResponse(restaurantService.updateRoom(id, roomId, roomRequest)));
    }

    @GetMapping("/{id}/rooms")
    public ResponseEntity<List<RoomResponse>> getRoomsForRestaurant(@PathVariable Long id) {
        List<Room> rooms = restaurantService.getRoomsForRestaurant(id);
        List<RoomResponse> roomResponses = rooms.stream().map(roomMapper::toResponse).toList();
        return ResponseEntity.ok(roomResponses);
    }
}
