package com.openclassrooms.starterjwt.security.services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void loadUserByUsername_shouldReturnUserDetails_whenUserExists() {
        User user = new User();
        user.setId(1L);
        user.setEmail("user@test.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("encoded-password");
        user.setAdmin(false);

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));

        UserDetailsImpl result = (UserDetailsImpl) userDetailsService.loadUserByUsername("user@test.com");

        assertEquals(1L, result.getId());
        assertEquals("user@test.com", result.getUsername());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("encoded-password", result.getPassword());
    }

    @Test
    void loadUserByUsername_shouldThrow_whenUserDoesNotExist() {
        when(userRepository.findByEmail("missing@test.com")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("missing@test.com"));

        assertEquals("User Not Found with email: missing@test.com", exception.getMessage());
    }
}
