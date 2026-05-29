package com.openclassrooms.starterjwt.security.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserDetailsImplTest {

    @Test
    void shouldExposeDefaultSecurityFlagsAndNoAuthorities() {
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("user@test.com")
                .firstName("John")
                .lastName("Doe")
                .admin(true)
                .password("secret")
                .build();

        assertTrue(userDetails.getAuthorities().isEmpty());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isEnabled());
    }

    @Test
    void shouldCompareUsersByIdOnly() {
        UserDetailsImpl first = UserDetailsImpl.builder().id(1L).username("first@test.com").build();
        UserDetailsImpl sameId = UserDetailsImpl.builder().id(1L).username("second@test.com").build();
        UserDetailsImpl otherId = UserDetailsImpl.builder().id(2L).username("other@test.com").build();

        assertEquals(first, sameId);
        assertFalse(first.equals(otherId));
    }
}
