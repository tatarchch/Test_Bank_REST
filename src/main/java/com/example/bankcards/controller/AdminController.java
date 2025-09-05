package com.example.bankcards.controller;

import com.example.bankcards.dto.UserDto;
import com.example.bankcards.service.AdminService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/getAll")
    public List<UserDto> getAll() {
        return adminService.getAllAdmins();
    }

    @PostMapping("/createNew")
    public UserDto addNewAdmin(@Valid @RequestBody UserDto userDto) {
        return adminService.addNewAdmin(userDto);
    }

}
