package com.example.generation.dtos;

import com.example.generation.dtos.ResponseDTOs.UserResponseDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserResponseDTOTest {

    @Test
    void UserResponseDTO_BuilderAndGettersWork() {
        long id = 1L;
        String firstName = "John";
        String lastName = "Doe";
        String role = "CUSTOMER";
        String status = "ACTIVE";

        UserResponseDTO dto = UserResponseDTO.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .role(role)
                .status(status)
                .build();

        assertEquals(id, dto.getId());
        assertEquals(firstName, dto.getFirstName());
        assertEquals(lastName, dto.getLastName());
        assertEquals(role, dto.getRole());
        assertEquals(status, dto.getStatus());
    }
}
