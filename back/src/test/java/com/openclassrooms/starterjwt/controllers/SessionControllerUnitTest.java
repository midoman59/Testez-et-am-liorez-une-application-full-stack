package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.services.SessionService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class SessionControllerUnitTest {

    @Test
    void findAll_and_findById_and_create_should_return_ok() {
        SessionService sessionService = Mockito.mock(SessionService.class);
        SessionMapper sessionMapper = Mockito.mock(SessionMapper.class);

        SessionController controller = new SessionController(sessionService, sessionMapper);

        Session s = new Session();
        s.setId(1L);
        s.setName("Yoga");

        SessionDto dto = new SessionDto();
        dto.setId(1L);
        dto.setName("Yoga");
        dto.setDate(new Date());
        dto.setTeacher_id(2L);
        dto.setDescription("desc");

        when(sessionService.findAll()).thenReturn(Collections.singletonList(s));
        when(sessionMapper.toDto(Collections.singletonList(s))).thenReturn(Collections.singletonList(dto));

        ResponseEntity<?> all = controller.findAll();
        assertEquals(200, all.getStatusCodeValue());
        assertNotNull(all.getBody());

        when(sessionService.getByIdOrThrow(1L)).thenReturn(s);
        when(sessionMapper.toDto(s)).thenReturn(dto);
        ResponseEntity<?> one = controller.findById(1L);
        assertEquals(200, one.getStatusCodeValue());

        when(sessionMapper.toEntity(dto)).thenReturn(s);
        when(sessionService.create(s)).thenReturn(s);
        when(sessionMapper.toDto(s)).thenReturn(dto);

        ResponseEntity<?> created = controller.create(dto);
        assertEquals(200, created.getStatusCodeValue());
        assertNotNull(created.getBody());
    }
}

