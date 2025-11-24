package com.assignment.private_dining_reservation_system.repository;

import com.assignment.private_dining_reservation_system.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
}
