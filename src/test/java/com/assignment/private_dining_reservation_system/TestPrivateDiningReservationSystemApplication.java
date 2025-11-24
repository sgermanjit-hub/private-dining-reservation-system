package com.assignment.private_dining_reservation_system;

import org.springframework.boot.SpringApplication;

public class TestPrivateDiningReservationSystemApplication {

    public static void main(String[] args) {
        SpringApplication.from(PrivateDiningReservationSystemApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
