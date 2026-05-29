package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.exception.UnauthorizedException;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void findById_shouldReturnUser_whenUserExists() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userRepository).findById(1L);
    }

    @Test
    void findById_shouldReturnNull_whenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        User result = userService.findById(1L);

        assertNull(result);
        verify(userRepository).findById(1L);
    }

    @Test
    void findByIdOrThrow_shouldReturnUser_whenUserExists() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.findByIdOrThrow(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findByIdOrThrow_shouldThrowNotFoundException_whenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.findByIdOrThrow(1L));
    }

    @Test
    void findByEmail_shouldReturnUser_whenUserExists() {
        User user = new User();
        user.setEmail("alice@test.com");
        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(user));

        User result = userService.findByEmail("alice@test.com");

        assertNotNull(result);
        assertEquals("alice@test.com", result.getEmail());
        verify(userRepository).findByEmail("alice@test.com");
    }

    @Test
    void findByEmail_shouldReturnNull_whenUserDoesNotExist() {
        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.empty());

        User result = userService.findByEmail("alice@test.com");

        assertNull(result);
        verify(userRepository).findByEmail("alice@test.com");
    }

    @Test
    void findByEmailOrThrow_shouldReturnUser_whenUserExists() {
        User user = new User();
        user.setEmail("alice@test.com");
        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(user));

        User result = userService.findByEmailOrThrow("alice@test.com");

        assertNotNull(result);
        assertEquals("alice@test.com", result.getEmail());
    }

    @Test
    void findByEmailOrThrow_shouldThrowNotFoundException_whenUserDoesNotExist() {
        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.findByEmailOrThrow("alice@test.com"));
    }

    @Test
    void existsByEmail_shouldReturnTrue_whenEmailExists() {
        when(userRepository.existsByEmail("alice@test.com")).thenReturn(true);

        Boolean result = userService.existsByEmail("alice@test.com");

        assertEquals(true, result);
        verify(userRepository).existsByEmail("alice@test.com");
    }

    @Test
    void existsByEmail_shouldReturnFalse_whenEmailDoesNotExist() {
        when(userRepository.existsByEmail("alice@test.com")).thenReturn(false);

        Boolean result = userService.existsByEmail("alice@test.com");

        assertFalse(result);
        verify(userRepository).existsByEmail("alice@test.com");
    }

    @Test
    void register_shouldCreateUser_whenEmailDoesNotExist() {
        SignupRequest request = new SignupRequest();
        request.setEmail("alice@test.com");
        request.setFirstName("Alice");
        request.setLastName("Martin");
        request.setPassword("password123");

        when(userRepository.existsByEmail("alice@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
        when(userRepository.save(org.mockito.ArgumentMatchers.any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.register(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertNotNull(result);
        assertEquals("alice@test.com", savedUser.getEmail());
        assertEquals("Alice", savedUser.getFirstName());
        assertEquals("Martin", savedUser.getLastName());
        assertEquals("encoded-password", savedUser.getPassword());
        assertFalse(savedUser.isAdmin());
    }

    @Test
    void register_shouldThrowBadRequestException_whenEmailAlreadyExists() {
        SignupRequest request = new SignupRequest();
        request.setEmail("alice@test.com");
        request.setFirstName("Alice");
        request.setLastName("Martin");
        request.setPassword("password123");

        when(userRepository.existsByEmail("alice@test.com")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> userService.register(request));
        verify(passwordEncoder, never()).encode(org.mockito.ArgumentMatchers.anyString());
        verify(userRepository, never()).save(org.mockito.ArgumentMatchers.any(User.class));
    }

    @Test
    void delete_shouldRemoveUser_whenEmailMatches() {
        User user = new User();
        user.setId(1L);
        user.setEmail("alice@test.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.delete(1L, "alice@test.com");

        verify(userRepository).deleteById(1L);
    }

    @Test
    void delete_shouldThrowUnauthorizedException_whenEmailDoesNotMatch() {
        User user = new User();
        user.setId(1L);
        user.setEmail("alice@test.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(UnauthorizedException.class, () -> userService.delete(1L, "bob@test.com"));
        verify(userRepository, never()).deleteById(1L);
    }

    @Test
    void delete_shouldThrowNotFoundException_whenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.delete(1L, "alice@test.com"));
        verify(userRepository, never()).deleteById(1L);
    }
}
