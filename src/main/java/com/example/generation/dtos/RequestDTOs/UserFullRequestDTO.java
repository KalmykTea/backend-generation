package com.example.generation.dtos.RequestDTOs;

import com.example.generation.framework.annotations.ValidBSN;
import com.example.generation.framework.annotations.ValidBirthDate;
import com.example.generation.framework.groups.OnCreate;
import com.example.generation.framework.groups.OnTransaction;
import com.example.generation.framework.groups.OnUpdate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserFullRequestDTO {

    @Null(groups = OnCreate.class, message = "ID must be null on creation")
    @NotNull(groups = {OnUpdate.class, OnTransaction.class}, message = "ID is required for updates")
    private Long id;

    @Valid
    private AddressRequestDTO address;

    @NotBlank(groups = {OnCreate.class, OnUpdate.class})
    @Size(min = 1, max = 50, groups = {OnCreate.class,  OnUpdate.class})
    @Pattern(regexp = "^[\\p{L}\\s\\-']+$", message = "Firstname contains invalid characters", groups = {OnCreate.class, OnUpdate.class})
    private String firstName;

    @NotBlank(groups = {OnCreate.class, OnUpdate.class})
    @Size(min = 1, max = 100,  groups = {OnCreate.class,  OnUpdate.class})
    @Pattern(regexp = "^[\\p{L}\\s\\-']+$", message = "Lastname contains invalid characters")
    private String lastName;

    @NotBlank(groups = {OnCreate.class, OnUpdate.class})
    @Email(message = "Please provide a valid email address", groups = {OnCreate.class, OnUpdate.class})
    private String email;

    @NotBlank(groups = { OnCreate.class, OnUpdate.class })
    @Size(min = 8, max = 128, groups = {OnCreate.class, OnUpdate.class})
    private String password;

    @ValidBSN(groups = {OnCreate.class, OnUpdate.class})
    private String bsnNumber;

    @ValidBirthDate(groups = {OnCreate.class, OnUpdate.class})
    private LocalDate birthdate;

    // Allowed spaces and hyphens for better UX
    @Pattern(regexp = "^0[1-9][0-9]{1,2}[- ]?\\d{6,7}$", message = "Invalid Dutch phone number", groups = {OnCreate.class, OnUpdate.class})
    private String phoneNumber;
}
