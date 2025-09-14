package com.example.bankcards.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserDto {

    private Long id;

    private String name;

    @NotNull
    private String username;

    @NotNull
    private String password;

    private String role;

}
