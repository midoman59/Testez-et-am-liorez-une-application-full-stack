package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SessionService sessionService;

    @Test
    void create_shouldSaveAndReturnSession() {
        Session session = new Session();
        session.setName("Yoga Matinal");
        session.setUsers(new ArrayList<>());
        when(sessionRepository.save(org.mockito.ArgumentMatchers.any(Session.class))).thenReturn(session);

        Session result = sessionService.create(session);

        assertNotNull(result);
        assertEquals("Yoga Matinal", result.getName());
        verify(sessionRepository).save(session);
    }

    @Test
    void delete_shouldDeleteSession_whenSessionExists() {
        Session session = new Session();
        session.setId(1L);
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        sessionService.delete(1L);

        verify(sessionRepository).deleteById(1L);
    }

    @Test
    void delete_shouldThrowNotFoundException_whenSessionDoesNotExist() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sessionService.delete(1L));
        verify(sessionRepository, never()).deleteById(org.mockito.ArgumentMatchers.anyLong());
    }

    @Test
    void findAll_shouldReturnAllSessions_whenSessionsExist() {
        Session session1 = new Session();
        session1.setId(1L);
        session1.setName("Yoga Matinal");

        Session session2 = new Session();
        session2.setId(2L);
        session2.setName("Yoga Soir");

        List<Session> sessions = new ArrayList<>();
        sessions.add(session1);
        sessions.add(session2);
        when(sessionRepository.findAll()).thenReturn(sessions);

        List<Session> result = sessionService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Yoga Matinal", result.get(0).getName());
        assertEquals("Yoga Soir", result.get(1).getName());
        verify(sessionRepository).findAll();
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoSessionsExist() {
        when(sessionRepository.findAll()).thenReturn(new ArrayList<>());

        List<Session> result = sessionService.findAll();

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(sessionRepository).findAll();
    }

    @Test
    void getById_shouldReturnSession_whenSessionExists() {
        Session session = new Session();
        session.setId(1L);
        session.setName("Yoga Matinal");
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        Session result = sessionService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Yoga Matinal", result.getName());
        verify(sessionRepository).findById(1L);
    }

    @Test
    void getById_shouldReturnNull_whenSessionDoesNotExist() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

        Session result = sessionService.getById(1L);

        assertNull(result);
        verify(sessionRepository).findById(1L);
    }

    @Test
    void getByIdOrThrow_shouldReturnSession_whenSessionExists() {
        Session session = new Session();
        session.setId(1L);
        session.setName("Yoga Matinal");
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        Session result = sessionService.getByIdOrThrow(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Yoga Matinal", result.getName());
    }

    @Test
    void getByIdOrThrow_shouldThrowNotFoundException_whenSessionDoesNotExist() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sessionService.getByIdOrThrow(1L));
    }

    @Test
    void update_shouldModifyAndReturnSession_whenSessionExists() {
        Session existingSession = new Session();
        existingSession.setId(1L);
        existingSession.setName("Old Name");

        Session updatedSession = new Session();
        updatedSession.setName("New Name");

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(existingSession));
        when(sessionRepository.save(org.mockito.ArgumentMatchers.any(Session.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Session result = sessionService.update(1L, updatedSession);

        assertEquals(1L, result.getId());
        assertEquals("New Name", result.getName());
        verify(sessionRepository).save(org.mockito.ArgumentMatchers.any(Session.class));
    }

    @Test
    void update_shouldThrowNotFoundException_whenSessionDoesNotExist() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());
        Session updatedSession = new Session();

        assertThrows(NotFoundException.class, () -> sessionService.update(1L, updatedSession));
        verify(sessionRepository, never()).save(org.mockito.ArgumentMatchers.any(Session.class));
    }

    @Test
    void participate_shouldAddUserToSession_whenUserNotAlreadyParticipating() {
        User user = new User();
        user.setId(1L);

        Session session = new Session();
        session.setId(1L);
        session.setUsers(new ArrayList<>());

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(sessionRepository.save(org.mockito.ArgumentMatchers.any(Session.class))).thenReturn(session);

        sessionService.participate(1L, 1L);

        ArgumentCaptor<Session> sessionCaptor = ArgumentCaptor.forClass(Session.class);
        verify(sessionRepository).save(sessionCaptor.capture());
        Session savedSession = sessionCaptor.getValue();
        assertEquals(1, savedSession.getUsers().size());
        assertEquals(1L, savedSession.getUsers().get(0).getId());
    }

    @Test
    void participate_shouldThrowNotFoundException_whenSessionDoesNotExist() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sessionService.participate(1L, 1L));
        verify(sessionRepository, never()).save(org.mockito.ArgumentMatchers.any(Session.class));
    }

    @Test
    void participate_shouldThrowNotFoundException_whenUserDoesNotExist() {
        Session session = new Session();
        session.setId(1L);
        session.setUsers(new ArrayList<>());

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sessionService.participate(1L, 1L));
        verify(sessionRepository, never()).save(org.mockito.ArgumentMatchers.any(Session.class));
    }

    @Test
    void participate_shouldThrowBadRequestException_whenUserAlreadyParticipating() {
        User user = new User();
        user.setId(1L);

        Session session = new Session();
        session.setId(1L);
        List<User> users = new ArrayList<>();
        users.add(user);
        session.setUsers(users);

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> sessionService.participate(1L, 1L));
        verify(sessionRepository, never()).save(org.mockito.ArgumentMatchers.any(Session.class));
    }

    @Test
    void noLongerParticipate_shouldRemoveUserFromSession_whenUserIsParticipating() {
        User user = new User();
        user.setId(1L);

        Session session = new Session();
        session.setId(1L);
        List<User> users = new ArrayList<>();
        users.add(user);
        session.setUsers(users);

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(sessionRepository.save(org.mockito.ArgumentMatchers.any(Session.class))).thenReturn(session);

        sessionService.noLongerParticipate(1L, 1L);

        ArgumentCaptor<Session> sessionCaptor = ArgumentCaptor.forClass(Session.class);
        verify(sessionRepository).save(sessionCaptor.capture());
        Session savedSession = sessionCaptor.getValue();
        assertEquals(0, savedSession.getUsers().size());
    }

    @Test
    void noLongerParticipate_shouldThrowNotFoundException_whenSessionDoesNotExist() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sessionService.noLongerParticipate(1L, 1L));
        verify(sessionRepository, never()).save(org.mockito.ArgumentMatchers.any(Session.class));
    }

    @Test
    void noLongerParticipate_shouldThrowBadRequestException_whenUserNotParticipating() {
        User user = new User();
        user.setId(2L);

        Session session = new Session();
        session.setId(1L);
        List<User> users = new ArrayList<>();
        users.add(user);
        session.setUsers(users);

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        assertThrows(BadRequestException.class, () -> sessionService.noLongerParticipate(1L, 1L));
        verify(sessionRepository, never()).save(org.mockito.ArgumentMatchers.any(Session.class));
    }
}

