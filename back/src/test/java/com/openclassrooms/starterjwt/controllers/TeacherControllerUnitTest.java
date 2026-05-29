package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.services.TeacherService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class TeacherControllerUnitTest {

    @Test
    void findAll_and_findById_should_return_ok() {
        TeacherService teacherService = Mockito.mock(TeacherService.class);
        TeacherMapper teacherMapper = Mockito.mock(TeacherMapper.class);

        TeacherController controller = new TeacherController(teacherService, teacherMapper);

        Teacher t = new Teacher();
        t.setId(3L);
        t.setFirstName("Jean");
        t.setLastName("Dupont");

        TeacherDto dto = new TeacherDto();
        dto.setId(3L);
        dto.setFirstName("Jean");
        dto.setLastName("Dupont");

        when(teacherService.findAll()).thenReturn(Collections.singletonList(t));
        when(teacherMapper.toDto(Collections.singletonList(t))).thenReturn(Collections.singletonList(dto));

        ResponseEntity<?> all = controller.findAll();
        assertEquals(200, all.getStatusCodeValue());
        assertNotNull(all.getBody());

        when(teacherService.findByIdOrThrow(3L)).thenReturn(t);
        when(teacherMapper.toDto(t)).thenReturn(dto);
        ResponseEntity<?> one = controller.findById(3L);
        assertEquals(200, one.getStatusCodeValue());
    }
}

