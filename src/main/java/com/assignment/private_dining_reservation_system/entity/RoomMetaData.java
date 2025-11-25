package com.assignment.private_dining_reservation_system.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.Set;

@Entity
@Table(name = "room_metadata")
@Getter
@Setter
public class RoomMetaData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roomId", nullable = false, unique = true)
    private Room room;

    @Column(name = "room_opening_time")
    private LocalTime roomOpeningTime;

    @Column(name = "room_closing_time")
    private LocalTime roomClosingTime;

    /**
     * It's kept at room level rather than restaurant, as it possible to have maintenance days for a room.
     * */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "room_open_days", joinColumns = @JoinColumn(name = "room_metadata_id"))
    @Column(name = "day_of_week")
    @Enumerated(EnumType.STRING)
    Set<DayOfWeek> openDays = EnumSet.allOf(DayOfWeek.class); //Default is room available for all days
}
