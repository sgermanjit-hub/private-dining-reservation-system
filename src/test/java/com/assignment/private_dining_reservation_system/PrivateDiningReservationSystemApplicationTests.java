package com.assignment.private_dining_reservation_system;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class PrivateDiningReservationSystemApplicationTests {

    @Test
    void contextLoads() {
    }

}

