package com.openclassrooms.starterjwt.mapper;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.models.User;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperListTest {

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    void toDtoList_and_toEntityList_should_work() {
        User u = new User();
        u.setId(11L);
        u.setEmail("a@test.com");
        u.setFirstName("A");
        u.setLastName("B");
        u.setPassword("p");
        u.setAdmin(false);

        List<UserDto> dtos = mapper.toDto(Collections.singletonList(u));
        assertNotNull(dtos);
        assertEquals(1, dtos.size());

        UserDto dto = dtos.get(0);
        dto.setPassword("p");
        List<User> us = mapper.toEntity(Collections.singletonList(dto));
        assertNotNull(us);
        assertEquals(1, us.size());
    }
}

