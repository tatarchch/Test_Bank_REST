package com.example.bankcards.service;

import com.example.bankcards.dto.UserDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.enums.UserRole;
import com.example.bankcards.exception.OtherException;
import com.example.bankcards.mapper.UserMapper;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminService adminService;

    private UserDto testUserDto;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUserDto = new UserDto();
        testUserDto.setId(1L);
        testUserDto.setName("adminUser");
        testUserDto.setUsername("adminUser");
        testUserDto.setPassword("rawPassword");
        testUserDto.setRole(UserRole.USER.getString());

        testUser = new User();
        testUser.setId(1L);
        testUser.setName("adminUser");
        testUser.setUsername("adminUser");
        testUser.setPassword("encodedPassword");
        testUser.setRole(UserRole.ADMIN.getString());
    }

    @Test
    void getAllAdminsShouldReturnListOfAdminUsers() {
        List<User> adminUsers = List.of(testUser);
        when(userRepository.findAllByRole(UserRole.ADMIN.getString())).thenReturn(adminUsers);
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);

        List<UserDto> result = adminService.getAllAdmins();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUserDto, result.get(0));
        verify(userRepository, times(1)).findAllByRole(UserRole.ADMIN.getString());
        verify(userMapper, times(1)).toDto(testUser);
    }

    @Test
    void addNewAdminShouldCreateAdminUserWithEncodedPassword() {
        UserDto inputUserDto = new UserDto();
        inputUserDto.setUsername("newAdmin");
        inputUserDto.setPassword("rawPassword");
        inputUserDto.setRole(UserRole.USER.getString());

        User userEntity = new User();
        userEntity.setUsername("newAdmin");
        userEntity.setPassword("rawPassword");
        userEntity.setRole(UserRole.USER.getString());

        User userWithEncodedPassword = new User();
        userWithEncodedPassword.setUsername("newAdmin");
        userWithEncodedPassword.setPassword("encodedPassword");
        userWithEncodedPassword.setRole(UserRole.ADMIN.getString());
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("newAdmin");
        savedUser.setPassword("encodedPassword");
        savedUser.setRole(UserRole.ADMIN.getString());

        UserDto resultUserDto = new UserDto();
        resultUserDto.setId(1L);
        resultUserDto.setUsername("newAdmin");
        resultUserDto.setRole(UserRole.ADMIN.getString());

        when(userMapper.toEntity(inputUserDto)).thenReturn(userEntity);
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toDto(savedUser)).thenReturn(resultUserDto);

        UserDto result = adminService.addNewAdmin(inputUserDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("newAdmin", result.getUsername());
        assertEquals(UserRole.ADMIN.getString(), result.getRole());

        verify(userMapper, times(1)).toEntity(inputUserDto);
        verify(passwordEncoder, times(1)).encode("rawPassword");
        verify(userRepository, times(1)).save(any(User.class));
        verify(userMapper, times(1)).toDto(savedUser);
    }

    @Test
    void addNewAdminShouldThrowExceptionWhenOptionalIsEmpty() {

        UserDto inputUserDto = new UserDto();
        inputUserDto.setUsername("newAdmin");
        inputUserDto.setPassword("rawPassword");

        when(userMapper.toEntity(inputUserDto)).thenReturn(null);

        assertThrows(OtherException.class, () -> adminService.addNewAdmin(inputUserDto));

        verify(userMapper, times(1)).toEntity(inputUserDto);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(userMapper, never()).toDto(any(User.class));
    }

    @Test
    void addNewAdminShouldSetAdminRole() {

        UserDto inputUserDto = new UserDto();
        inputUserDto.setUsername("newAdmin");
        inputUserDto.setPassword("rawPassword");
        inputUserDto.setRole(UserRole.USER.getString());

        User userEntity = new User();
        userEntity.setUsername("newAdmin");
        userEntity.setPassword("rawPassword");
        userEntity.setRole(UserRole.USER.getString());

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("newAdmin");
        savedUser.setPassword("encodedPassword");
        savedUser.setRole(UserRole.ADMIN.getString());

        UserDto resultUserDto = new UserDto();
        resultUserDto.setId(1L);
        resultUserDto.setUsername("newAdmin");
        resultUserDto.setRole(UserRole.ADMIN.getString());

        when(userMapper.toEntity(inputUserDto)).thenReturn(userEntity);
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toDto(savedUser)).thenReturn(resultUserDto);

        UserDto result = adminService.addNewAdmin(inputUserDto);

        assertEquals(UserRole.ADMIN.getString(), result.getRole());
    }

}
