package com.example.generation.dtos.RequestDTOs;

import com.example.generation.framework.groups.OnCreate;
import com.example.generation.framework.groups.OnUpdate;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {

    @NotBlank(groups = {OnCreate.class, OnUpdate.class})
    @Email(message = "Please provide a valid email address", groups = {OnCreate.class, OnUpdate.class})
    private String email;

    @NotBlank(groups = { OnCreate.class, OnUpdate.class })
    @Size(min = 8, max = 128, groups = {OnCreate.class, OnUpdate.class})
    private String password;
}
