package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeacherServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private TeacherService teacherService;

    @Test
    void findAll_shouldReturnAllTeachers_whenTeachersExist() {
        Teacher teacher1 = new Teacher();
        teacher1.setId(1L);
        teacher1.setFirstName("Jean");
        teacher1.setLastName("Dupont");

        Teacher teacher2 = new Teacher();
        teacher2.setId(2L);
        teacher2.setFirstName("Marie");
        teacher2.setLastName("Martin");

        List<Teacher> teachers = new ArrayList<>();
        teachers.add(teacher1);
        teachers.add(teacher2);
        when(teacherRepository.findAll()).thenReturn(teachers);

        List<Teacher> result = teacherService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Jean", result.get(0).getFirstName());
        assertEquals("Marie", result.get(1).getFirstName());
        verify(teacherRepository).findAll();
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoTeachersExist() {
        when(teacherRepository.findAll()).thenReturn(new ArrayList<>());

        List<Teacher> result = teacherService.findAll();

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(teacherRepository).findAll();
    }

    @Test
    void findById_shouldReturnTeacher_whenTeacherExists() {
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setFirstName("Jean");
        teacher.setLastName("Dupont");
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));

        Teacher result = teacherService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Jean", result.getFirstName());
        verify(teacherRepository).findById(1L);
    }

    @Test
    void findById_shouldReturnNull_whenTeacherDoesNotExist() {
        when(teacherRepository.findById(1L)).thenReturn(Optional.empty());

        Teacher result = teacherService.findById(1L);

        assertNull(result);
        verify(teacherRepository).findById(1L);
    }

    @Test
    void findByIdOrThrow_shouldReturnTeacher_whenTeacherExists() {
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setFirstName("Jean");
        teacher.setLastName("Dupont");
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));

        Teacher result = teacherService.findByIdOrThrow(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Jean", result.getFirstName());
    }

    @Test
    void findByIdOrThrow_shouldThrowNotFoundException_whenTeacherDoesNotExist() {
        when(teacherRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> teacherService.findByIdOrThrow(1L));
        verify(teacherRepository).findById(1L);
    }
}

