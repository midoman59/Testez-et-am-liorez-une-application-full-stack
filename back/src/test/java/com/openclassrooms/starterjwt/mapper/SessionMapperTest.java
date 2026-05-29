package com.openclassrooms.starterjwt.mapper;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.TeacherService;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

class SessionMapperTest {

    private SessionMapper mapper;
    private TeacherService teacherService;
    private UserService userService;

    @BeforeEach
    void setUp() throws Exception {
        mapper = Mappers.getMapper(SessionMapper.class);

        // set services by reflection on implementation
        teacherService = Mockito.mock(TeacherService.class);
        userService = Mockito.mock(UserService.class);

        // try to set fields on mapper implementation
        Object impl = mapper;
        // the fields are declared in the abstract mapper superclass; getDeclaredField on superclass
        java.lang.reflect.Field f1 = impl.getClass().getSuperclass().getDeclaredField("teacherService");
        f1.setAccessible(true);
        f1.set(impl, teacherService);
        java.lang.reflect.Field f2 = impl.getClass().getSuperclass().getDeclaredField("userService");
        f2.setAccessible(true);
        f2.set(impl, userService);

        Teacher t = new Teacher();
        t.setId(10L);
        t.setFirstName("T");
        t.setLastName("L");

        when(teacherService.findById(anyLong())).thenReturn(t);

        User u = new User();
        u.setId(20L);
        u.setEmail("u@test.com");
        when(userService.findById(anyLong())).thenReturn(u);
    }

    @Test
    void toEntity_should_map_teacher_and_users() {
        SessionDto dto = new SessionDto();
        dto.setId(5L);
        dto.setName("Yoga");
        dto.setDate(new Date());
        dto.setTeacher_id(10L);
        dto.setUsers(Arrays.asList(20L));
        dto.setDescription("desc");

        Session s = mapper.toEntity(dto);
        assertNotNull(s);
        assertNotNull(s.getTeacher());
        assertEquals(10L, s.getTeacher().getId());
        assertNotNull(s.getUsers());
        assertEquals(1, s.getUsers().size());
    }

    @Test
    void toDto_should_map_users_and_teacher_id() {
        Session s = new Session();
        s.setId(6L);
        s.setName("S");
        s.setDescription("d");
        Teacher t = new Teacher();
        t.setId(11L);
        s.setTeacher(t);
        User u = new User();
        u.setId(21L);
        s.setUsers(Collections.singletonList(u));

        SessionDto dto = mapper.toDto(s);
        assertNotNull(dto);
        assertEquals(11L, dto.getTeacher_id());
        assertNotNull(dto.getUsers());
        assertEquals(1, dto.getUsers().size());
    }

    @Test
    void toEntity_should_handle_null_users_and_teacher() {
        SessionDto dto = new SessionDto();
        dto.setId(7L);
        dto.setName("Y");
        dto.setDate(new Date());
        dto.setTeacher_id(null);
        dto.setUsers(null);
        dto.setDescription("d");

        Session s = mapper.toEntity(dto);
        assertNotNull(s);
        // teacher is null branch
        assertNull(s.getTeacher());
        // users should be empty list
        assertNotNull(s.getUsers());
    }

    @Test
    void toDto_should_handle_null_users() {
        Session s = new Session();
        s.setId(8L);
        s.setName("S");
        s.setDescription("d");
        s.setTeacher(null);
        s.setUsers(null);

        SessionDto dto = mapper.toDto(s);
        assertNotNull(dto);
        assertNull(dto.getTeacher_id());
        assertNotNull(dto.getUsers());
        assertTrue(dto.getUsers().isEmpty());
    }
}


