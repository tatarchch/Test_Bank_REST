package com.example.bankcards.controller;

import com.example.bankcards.dto.UserDto;
import com.example.bankcards.service.UserService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserDto testUserDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();

        testUserDto = new UserDto();
        testUserDto.setId(1L);
        testUserDto.setUsername("testUser");
        testUserDto.setPassword("password123");
    }

    @Test
    void getAllUsersShouldReturnListOfUsers() throws Exception {
        List<UserDto> users = List.of(testUserDto);
        when(userService.getAllUsers()).thenReturn(users);
        mockMvc.perform(get("/api/v1/user/getAll")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].username").value("testUser"));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void getUserByIdShouldReturnUser() throws Exception {
        when(userService.getUserById(1L)).thenReturn(testUserDto);

        mockMvc.perform(get("/api/v1/user/getById/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testUser"));

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void getUserByUsernameAndPasswordShouldReturnUser() throws Exception {
        when(userService.getUserByUsernameAndPassword("testUser", "password123")).thenReturn(testUserDto);

        mockMvc.perform(get("/api/v1/user/getByUsernameAndPassword")
                        .param("username", "testUser")
                        .param("password", "password123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testUser"));

        verify(userService, times(1)).getUserByUsernameAndPassword("testUser", "password123");
    }

    @Test
    void addNewUserShouldReturnCreatedUser() throws Exception {
        when(userService.addNewUser(any(UserDto.class))).thenReturn(testUserDto);

        mockMvc.perform(post("/api/v1/user/createNew")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testUser"));

        verify(userService, times(1)).addNewUser(any(UserDto.class));
    }

    @Test
    void deleteUserShouldCallService() throws Exception {
        doNothing().when(userService).deleteUser(any(UserDto.class));

        mockMvc.perform(delete("/api/v1/user/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUserDto)))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUser(any(UserDto.class));
    }

    @Test
    void deleteUserByIdShouldCallService() throws Exception {
        doNothing().when(userService).deleteUserById(1L);

        mockMvc.perform(delete("/api/v1/user/deleteById/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUserById(1L);
    }

}
