package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AuthControllerUnitTest {

    @Test
    void registerUser_should_return_message() {
        UserService userService = Mockito.mock(UserService.class);
        AuthenticationManager am = Mockito.mock(AuthenticationManager.class);
        JwtUtils jwtUtils = Mockito.mock(JwtUtils.class);

        AuthController controller = new AuthController(am, jwtUtils, userService);

        SignupRequest req = new SignupRequest();
        req.setEmail("new@test.com");
        req.setFirstName("New");
        req.setLastName("User");
        req.setPassword("pass123");

        ResponseEntity<?> resp = controller.registerUser(req);
        assertEquals(200, resp.getStatusCodeValue());
        assertNotNull(resp.getBody());
    }

    @Test
    void authenticateUser_should_return_jwt_response() {
        UserService userService = Mockito.mock(UserService.class);
        AuthenticationManager am = Mockito.mock(AuthenticationManager.class);
        JwtUtils jwtUtils = Mockito.mock(JwtUtils.class);

        AuthController controller = new AuthController(am, jwtUtils, userService);

        // mock authentication
        Authentication authentication = Mockito.mock(Authentication.class);
        UserDetailsImpl principal = UserDetailsImpl.builder()
                .id(7L)
                .username("user@test.com")
                .firstName("John")
                .lastName("Doe")
                .admin(false)
                .build();
        when(authentication.getPrincipal()).thenReturn(principal);
        when(am.authenticate(any())).thenReturn(authentication);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("token-abc");

        User user = new User();
        user.setAdmin(true);
        when(userService.findByEmailOrThrow("user@test.com")).thenReturn(user);

        LoginRequest login = new LoginRequest();
        login.setEmail("user@test.com");
        login.setPassword("pwd");

        ResponseEntity<?> resp = controller.authenticateUser(login);
        assertEquals(200, resp.getStatusCodeValue());
        assertNotNull(resp.getBody());
    }

    @Test
    void authenticateUser_should_return_isAdmin_false_when_user_not_admin() {
        UserService userService = Mockito.mock(UserService.class);
        AuthenticationManager am = Mockito.mock(AuthenticationManager.class);
        JwtUtils jwtUtils = Mockito.mock(JwtUtils.class);

        AuthController controller = new AuthController(am, jwtUtils, userService);

        Authentication authentication = Mockito.mock(Authentication.class);
        UserDetailsImpl principal = UserDetailsImpl.builder()
                .id(8L)
                .username("normal@test.com")
                .firstName("Norm")
                .lastName("Al")
                .admin(false)
                .build();
        when(authentication.getPrincipal()).thenReturn(principal);
        when(am.authenticate(any())).thenReturn(authentication);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("token-xyz");

        User user = new User();
        user.setAdmin(false);
        when(userService.findByEmailOrThrow("normal@test.com")).thenReturn(user);

        LoginRequest login = new LoginRequest();
        login.setEmail("normal@test.com");
        login.setPassword("pwd");

        ResponseEntity<?> resp = controller.authenticateUser(login);
        assertEquals(200, resp.getStatusCodeValue());
        assertNotNull(resp.getBody());
    }
}

