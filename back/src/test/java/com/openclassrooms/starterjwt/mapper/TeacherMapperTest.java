package com.openclassrooms.starterjwt.mapper;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.models.Teacher;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class TeacherMapperTest {

	private final TeacherMapper mapper = Mappers.getMapper(TeacherMapper.class);

	@Test
	void toDto_and_toEntity_should_map_fields() {
		Teacher t = new Teacher();
		t.setId(2L);
		t.setFirstName("Jean");
		t.setLastName("Dupont");

		TeacherDto dto = mapper.toDto(t);

		assertNotNull(dto);
		assertEquals(t.getId(), dto.getId());
		assertEquals(t.getFirstName(), dto.getFirstName());
		assertEquals(t.getLastName(), dto.getLastName());

		Teacher back = mapper.toEntity(dto);
		assertNotNull(back);
		assertEquals(dto.getFirstName(), back.getFirstName());
	}
}


