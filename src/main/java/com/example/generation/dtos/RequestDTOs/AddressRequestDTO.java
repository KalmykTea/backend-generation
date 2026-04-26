package com.example.generation.dtos.RequestDTOs;

import com.example.generation.framework.groups.OnCreate;
import com.example.generation.framework.groups.OnUpdate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddressRequestDTO {

    @Null(groups = OnCreate.class, message = "ID must be null on creation")
    @NotNull(groups = OnUpdate.class, message = "ID is required for updates")
    private Long id;

    @NotBlank(groups = {OnCreate.class, OnUpdate.class})
    @Size(min = 1, max = 128, groups = {OnCreate.class, OnUpdate.class})
    private String addressLine;
    @NotBlank(groups = {OnCreate.class, OnUpdate.class})
    @Size(min = 4, max = 10, message = "Postal code length is invalid", groups = {OnCreate.class, OnUpdate.class})
    private String postalCode;
    @NotBlank(groups = {OnCreate.class, OnUpdate.class})
    @Size(min = 1, max = 60,  groups = {OnCreate.class, OnUpdate.class})
    private String city;
    @NotBlank(groups = {OnCreate.class, OnUpdate.class})
    @Size(min = 1, max = 60, groups = {OnCreate.class, OnUpdate.class})
    private String country;
}
