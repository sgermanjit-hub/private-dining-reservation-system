package com.assignment.private_dining_reservation_system.repository;

import com.assignment.private_dining_reservation_system.entity.Reservation;
import com.assignment.private_dining_reservation_system.entity.ReservationStatus;
import com.assignment.private_dining_reservation_system.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("""
            select r from Reservation r
            where r.room = :room
            and r.reservationDate = :reservationDate
            and r.reservationStatus = :reservationStatus
            """)
    List<Reservation> findByRoomReservationDateAndStatus(@Param("room") Room room,
                                                         @Param("reservationDate") LocalDate reservationDate,
                                                         @Param("reservationStatus") ReservationStatus reservationStatus);

    List<Reservation> findByDinerEmail(String dinerEmail);

    List<Reservation> findByRestaurantId(Long restaurantId);

    List<Reservation> findByDinerEmailAndRestaurantId(String dinerEmail, Long restaurantId);
}
