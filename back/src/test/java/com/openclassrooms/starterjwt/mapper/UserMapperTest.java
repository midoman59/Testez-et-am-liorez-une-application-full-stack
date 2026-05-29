package com.openclassrooms.starterjwt.mapper;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.models.User;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    void toDto_and_toEntity_should_map_fields() {
        User user = new User();
        user.setId(1L);
        user.setEmail("user@test.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setAdmin(false);

        UserDto dto = mapper.toDto(user);

        assertNotNull(dto);
        assertEquals(user.getId(), dto.getId());
        assertEquals(user.getEmail(), dto.getEmail());
        assertEquals(user.getFirstName(), dto.getFirstName());
        assertEquals(user.getLastName(), dto.getLastName());

        // toEntity expects a password (model builder marks password NonNull) -> set a dummy password on dto
        dto.setPassword("pwd");
        User back = mapper.toEntity(dto);
        assertNotNull(back);
        assertEquals(dto.getEmail(), back.getEmail());
    }
}


