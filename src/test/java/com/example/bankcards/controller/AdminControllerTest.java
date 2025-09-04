package com.example.bankcards.controller;

import com.example.bankcards.dto.UserDto;
import com.example.bankcards.service.AdminService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@WebMvcTest
@ExtendWith(MockitoExtension.class)
public class AdminControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    private UserDto testUserDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
        objectMapper = new ObjectMapper();

        testUserDto = new UserDto();
        testUserDto.setId(1L);
        testUserDto.setUsername("adminUser");
        testUserDto.setPassword("adminPassword");
    }

    @Test
    void getAllShouldReturnListOfAdmins() throws Exception {

        List<UserDto> admins = List.of(testUserDto);
        when(adminService.getAllAdmins()).thenReturn(admins);

        mockMvc.perform(get("/api/v1/admin/getAll")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].username").value("adminUser"));

        verify(adminService, times(1)).getAllAdmins();
    }

    @Test
    void addNewAdminShouldReturnCreatedAdmin() throws Exception {

        when(adminService.addNewAdmin(any(UserDto.class))).thenReturn(testUserDto);

        mockMvc.perform(post("/api/v1/admin/createNew")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("adminUser"));

        verify(adminService, times(1)).addNewAdmin(any(UserDto.class));
    }

    @Test
    void addNewAdminWithInvalidDataShouldReturnBadRequest() throws Exception {
        UserDto invalidUserDto = new UserDto();

        mockMvc.perform(post("/api/v1/admin/createNew")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUserDto)))
                .andExpect(status().isBadRequest());

        verify(adminService, never()).addNewAdmin(any(UserDto.class));
    }

}
