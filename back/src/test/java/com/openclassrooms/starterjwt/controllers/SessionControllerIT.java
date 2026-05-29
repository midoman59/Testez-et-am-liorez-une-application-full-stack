package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Date;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(username = "user@test.com", roles = "USER")
class SessionControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Teacher teacher;
    private Teacher teacherForCreate;
    private User user1;
    private User user2;
    private Session session;

    @BeforeEach
    void setUp() {
        sessionRepository.deleteAll();
        userRepository.deleteAll();
        teacherRepository.deleteAll();

        teacher = new Teacher();
        teacher.setLastName("Dupont");
        teacher.setFirstName("Jean");
        teacherRepository.save(teacher);

        teacherForCreate = new Teacher();
        teacherForCreate.setLastName("Martin");
        teacherForCreate.setFirstName("Marie");
        teacherRepository.save(teacherForCreate);

        user1 = new User();
        user1.setEmail("user1@test.com");
        user1.setFirstName("User");
        user1.setLastName("One");
        user1.setPassword("password123");
        user1.setAdmin(false);
        userRepository.save(user1);

        user2 = new User();
        user2.setEmail("user2@test.com");
        user2.setFirstName("User");
        user2.setLastName("Two");
        user2.setPassword("password123");
        user2.setAdmin(false);
        userRepository.save(user2);

        session = new Session();
        session.setName("Yoga Matinal");
        session.setDate(new Date());
        session.setDescription("Description du yoga matinal");
        session.setTeacher(teacher);
        session.setUsers(new ArrayList<>());
        sessionRepository.save(session);
    }

    @Test
    void findById_shouldReturnSession_whenSessionExists() throws Exception {
        mockMvc.perform(get("/api/session/" + session.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(session.getId()))
                .andExpect(jsonPath("$.name").value("Yoga Matinal"))
                .andExpect(jsonPath("$.description").value("Description du yoga matinal"));
    }

    @Test
    void findById_shouldReturnNotFound_whenSessionDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/session/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAll_shouldReturnAllSessions() throws Exception {
        mockMvc.perform(get("/api/session"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Yoga Matinal"));
    }

    @Test
    void create_shouldCreateSession_whenValidData() throws Exception {
        SessionDto createDto = new SessionDto();
        createDto.setName("Yoga Soir");
        createDto.setDate(new Date());
        createDto.setDescription("Description du yoga soir");
        createDto.setTeacher_id(teacherForCreate.getId());

        mockMvc.perform(post("/api/session")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Yoga Soir"))
                .andExpect(jsonPath("$.id").value(notNullValue()));
    }

    @Test
    void update_shouldUpdateSession_whenSessionExists() throws Exception {
        SessionDto updateDto = new SessionDto();
        updateDto.setName("Yoga Matinal Updated");
        updateDto.setDate(new Date());
        updateDto.setDescription("Updated description");
        updateDto.setTeacher_id(teacherForCreate.getId());

        mockMvc.perform(put("/api/session/" + session.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Yoga Matinal Updated"));
    }

    @Test
    void update_shouldReturnNotFound_whenSessionDoesNotExist() throws Exception {
        SessionDto updateDto = new SessionDto();
        updateDto.setName("Yoga Soir");
        updateDto.setDate(new Date());
        updateDto.setDescription("Description");
        updateDto.setTeacher_id(teacher.getId());

        mockMvc.perform(put("/api/session/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_shouldDeleteSession_whenSessionExists() throws Exception {
        mockMvc.perform(delete("/api/session/" + session.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void delete_shouldReturnNotFound_whenSessionDoesNotExist() throws Exception {
        mockMvc.perform(delete("/api/session/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void participate_shouldAddUserToSession_whenUserNotAlreadyParticipating() throws Exception {
        mockMvc.perform(post("/api/session/" + session.getId() + "/participate/" + user1.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/session/" + session.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.users", hasSize(1)))
                .andExpect(jsonPath("$.users[0]").value(user1.getId()));
    }

    @Test
    void participate_shouldReturnNotFound_whenSessionDoesNotExist() throws Exception {
        mockMvc.perform(post("/api/session/999/participate/" + user1.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void participate_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        mockMvc.perform(post("/api/session/" + session.getId() + "/participate/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void participate_shouldReturnBadRequest_whenUserAlreadyParticipating() throws Exception {
        session.getUsers().add(user1);
        sessionRepository.save(session);

        mockMvc.perform(post("/api/session/" + session.getId() + "/participate/" + user1.getId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void noLongerParticipate_shouldRemoveUserFromSession_whenUserIsParticipating() throws Exception {
        session.getUsers().add(user1);
        sessionRepository.save(session);

        mockMvc.perform(delete("/api/session/" + session.getId() + "/participate/" + user1.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/session/" + session.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.users", hasSize(0)));
    }

    @Test
    void noLongerParticipate_shouldReturnNotFound_whenSessionDoesNotExist() throws Exception {
        mockMvc.perform(delete("/api/session/999/participate/" + user1.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void noLongerParticipate_shouldReturnBadRequest_whenUserNotParticipating() throws Exception {
        mockMvc.perform(delete("/api/session/" + session.getId() + "/participate/" + user1.getId()))
                .andExpect(status().isBadRequest());
    }
}

