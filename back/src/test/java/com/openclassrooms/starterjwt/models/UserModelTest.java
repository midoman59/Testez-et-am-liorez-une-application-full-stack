package com.openclassrooms.starterjwt.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserModelTest {

    @Test
    void user_getters_setters_and_equals() {
        User u1 = new User();
        u1.setId(100L);
        u1.setEmail("ex@test.com");
        u1.setFirstName("F");
        u1.setLastName("L");
        u1.setPassword("p");
        u1.setAdmin(true);

        User u2 = new User();
        u2.setId(100L);
        u2.setEmail("ex2@test.com");
        u2.setFirstName("X");
        u2.setLastName("Y");
        u2.setPassword("p2");
        u2.setAdmin(false);

        assertEquals(u1, u2);
        assertTrue(u1.toString().contains("ex@test.com") || u1.toString().contains("ex2@test.com"));
    }
}

