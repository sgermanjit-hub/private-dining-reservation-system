package com.assignment.private_dining_reservation_system.repository;

import com.assignment.private_dining_reservation_system.entity.Room;
import com.assignment.private_dining_reservation_system.entity.RoomCalendar;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface RoomCalendarRepository extends JpaRepository<RoomCalendar, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select rc from RoomCalendar rc
            where rc.room = :room
            and rc.reservationDate = :reservationDate
            """)
    Optional<RoomCalendar> findByRoomAndReservationDateForUpdate(@Param("room") Room room,
                                                                 @Param("reservationDate") LocalDate reservationDate);

}
