package com.example.generation.dtos;

import com.example.generation.dtos.ResponseDTOs.AddressResponseDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AddressResponseDTOTest {

    @Test
    void AddressResponseDTO_BuilderAndGettersWork() {
        long id = 1L;
        String addressLine = "123 Main St";
        String postalCode = "1234 AB";
        String city = "Amsterdam";
        String country = "Netherlands";

        AddressResponseDTO dto = AddressResponseDTO.builder()
                .id(id)
                .addressLine(addressLine)
                .postalCode(postalCode)
                .city(city)
                .country(country)
                .build();

        assertEquals(id, dto.getId());
        assertEquals(addressLine, dto.getAddressLine());
        assertEquals(postalCode, dto.getPostalCode());
        assertEquals(city, dto.getCity());
        assertEquals(country, dto.getCountry());
    }
}
