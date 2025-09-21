package com.holistic.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserDto(
        Long id,

        @NotBlank(message = "Name is mandatory")
        String name,

        @NotBlank(message = "Email is mandatory")
        @Email(message = "Email should be valid")
        String email,
        
        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        String password
) {}