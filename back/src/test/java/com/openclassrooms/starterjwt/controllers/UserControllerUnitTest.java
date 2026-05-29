package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.mapper.UserMapper;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class UserControllerUnitTest {

    @Test
    void findById_and_delete_should_return_ok() {
        UserService userService = Mockito.mock(UserService.class);
        UserMapper userMapper = Mockito.mock(UserMapper.class);

        UserController controller = new UserController(userService, userMapper);

        User u = new User();
        u.setId(4L);
        u.setEmail("user@test.com");

        UserDto dto = new UserDto();
        dto.setId(4L);
        dto.setEmail("user@test.com");
        dto.setFirstName("John");
        dto.setLastName("Doe");

        when(userService.findByIdOrThrow(4L)).thenReturn(u);
        when(userMapper.toDto(u)).thenReturn(dto);

        ResponseEntity<?> one = controller.findById(4L);
        assertEquals(200, one.getStatusCodeValue());

        // prepare security context
        UserDetails userDetails = new org.springframework.security.core.userdetails.User("user@test.com","pwd", java.util.Collections.emptyList());
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContext sc = new SecurityContextImpl(authentication);
        SecurityContextHolder.setContext(sc);

        // delete
        ResponseEntity<?> del = controller.save(4L);
        assertEquals(200, del.getStatusCodeValue());
        // clear security context to avoid interfering other tests
        SecurityContextHolder.clearContext();
    }
}


