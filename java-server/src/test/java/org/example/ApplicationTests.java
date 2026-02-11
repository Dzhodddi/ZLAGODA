package org.example;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(properties = {"spring.profiles.active=test",
        "FRONT_URL=http://localhost:3000"})
@Testcontainers
class ApplicationTests {

    @Test
    void contextLoads() {
    }

}
