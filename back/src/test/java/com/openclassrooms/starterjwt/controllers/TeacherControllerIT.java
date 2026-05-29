package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(username = "user@test.com", roles = "USER")
class TeacherControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private SessionRepository sessionRepository;

    private Teacher teacher1;
    private Teacher teacher2;

    @BeforeEach
    void setUp() {
        sessionRepository.deleteAll();
        teacherRepository.deleteAll();

        teacher1 = new Teacher();
        teacher1.setLastName("Dupont");
        teacher1.setFirstName("Jean");
        teacherRepository.save(teacher1);

        teacher2 = new Teacher();
        teacher2.setLastName("Martin");
        teacher2.setFirstName("Marie");
        teacherRepository.save(teacher2);
    }

    @Test
    void findAll_shouldReturnAllTeachers() throws Exception {
        mockMvc.perform(get("/api/teacher"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName").value("Jean"))
                .andExpect(jsonPath("$[1].firstName").value("Marie"));
    }

    @Test
    void findById_shouldReturnTeacher_whenTeacherExists() throws Exception {
        mockMvc.perform(get("/api/teacher/" + teacher1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(teacher1.getId()))
                .andExpect(jsonPath("$.firstName").value("Jean"))
                .andExpect(jsonPath("$.lastName").value("Dupont"));
    }

    @Test
    void findById_shouldReturnNotFound_whenTeacherDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/teacher/999"))
                .andExpect(status().isNotFound());
    }
}

