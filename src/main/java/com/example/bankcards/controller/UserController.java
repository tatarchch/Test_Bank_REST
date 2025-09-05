package com.example.bankcards.controller;

import com.example.bankcards.dto.UserDto;
import com.example.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/getAll")
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/getById/{userId}")
    public UserDto getUserById(@PathVariable("userId") Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/getByUsernameAndPassword")
    public UserDto getUserByUsernameAndPassword(@RequestParam("username") String username, @RequestParam("password") String password) {
        return userService.getUserByUsernameAndPassword(username, password);
    }

    @PostMapping("/createNew")
    public UserDto addNewUser(@RequestBody UserDto userDto) {
        return userService.addNewUser(userDto);
    }

    @DeleteMapping("/delete")
    public void deleteUser(@RequestBody UserDto userDto) {
        userService.deleteUser(userDto);
    }

    @DeleteMapping("/deleteById/{userId}")
    public void deleteUserById(@PathVariable("userId") Long id) {
        userService.deleteUserById(id);
    }

}
