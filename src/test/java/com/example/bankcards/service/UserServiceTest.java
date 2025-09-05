package com.example.bankcards.service;

import com.example.bankcards.dto.UserDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.enums.UserRole;
import com.example.bankcards.exception.UserAlreadyExistsException;
import com.example.bankcards.exception.UserNotFoundException;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserDto testUserDto;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUserDto = new UserDto();
        testUserDto.setId(1L);
        testUserDto.setName("testUser");
        testUserDto.setUsername("testUser");
        testUserDto.setPassword("rawPassword");
        testUserDto.setRole(UserRole.USER.getString());

        testUser = new User();
        testUser.setId(1L);
        testUser.setName("testUser");
        testUser.setUsername("testUser");
        testUser.setPassword("encodedPassword");
        testUser.setRole(UserRole.USER.getString());
    }

    @Test
    void getAllUsersShouldReturnListOfUsers() {
        List<User> users = List.of(testUser);
        when(userRepository.findAllByRole(UserRole.USER.getString())).thenReturn(users);
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);

        List<UserDto> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUserDto, result.get(0));
        verify(userRepository, times(1)).findAllByRole(UserRole.USER.getString());
        verify(userMapper, times(1)).toDto(testUser);
    }

    @Test
    void getUserByIdShouldReturnUserWhenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);

        UserDto result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(testUserDto, result);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserById_ShouldThrowExceptionWhenUserNotExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(1L));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserByUsernameAndPasswordShouldReturnUserWhenCredentialsMatch() {
        when(userRepository.findUserByUsernameAndRole("testUser", UserRole.USER.getString()))
                .thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("rawPassword", "encodedPassword")).thenReturn(true);
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);

        UserDto result = userService.getUserByUsernameAndPassword("testUser", "rawPassword");

        assertNotNull(result);
        assertEquals(testUserDto, result);
        verify(userRepository, times(1)).findUserByUsernameAndRole("testUser", UserRole.USER.getString());
        verify(passwordEncoder, times(1)).matches("rawPassword", "encodedPassword");
    }

    @Test
    void getUserByUsernameAndPasswordShouldThrowException_WhenUserNotFound() {
        when(userRepository.findUserByUsernameAndRole("testUser", UserRole.USER.getString()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.getUserByUsernameAndPassword("testUser", "rawPassword"));
        verify(userRepository, times(1)).findUserByUsernameAndRole("testUser", UserRole.USER.getString());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void getUserByUsernameAndPasswordShouldThrowException_WhenPasswordDoesNotMatch() {
        when(userRepository.findUserByUsernameAndRole("testUser", UserRole.USER.getString()))
                .thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        assertThrows(UserNotFoundException.class,
                () -> userService.getUserByUsernameAndPassword("testUser", "wrongPassword"));
        verify(userRepository, times(1)).findUserByUsernameAndRole("testUser", UserRole.USER.getString());
        verify(passwordEncoder, times(1)).matches("wrongPassword", "encodedPassword");
    }

    @Test
    void addNewUser_ShouldCreateUserWithEncodedPassword_WhenUsernameIsUnique() {
        UserDto inputUserDto = new UserDto();
        inputUserDto.setName("newUSer");
        inputUserDto.setUsername("newUser");
        inputUserDto.setPassword("rawPassword");

        User userEntity = new User();
        userEntity.setName("newUser");
        userEntity.setUsername("newUser");
        userEntity.setPassword("rawPassword");

        User userWithEncodedPassword = new User();
        userWithEncodedPassword.setUsername("newUser");
        userWithEncodedPassword.setPassword("encodedPassword");
        userWithEncodedPassword.setRole(UserRole.USER.getString());

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("newUser");
        savedUser.setUsername("newUser");
        savedUser.setPassword("encodedPassword");
        savedUser.setRole(UserRole.USER.getString());

        UserDto resultUserDto = new UserDto();
        resultUserDto.setId(1L);
        resultUserDto.setName("newUser");
        resultUserDto.setUsername("newUser");
        resultUserDto.setRole(UserRole.USER.getString());

        when(userRepository.existsUserByUsername("newUser")).thenReturn(false);
        when(userMapper.toEntity(inputUserDto)).thenReturn(userEntity);
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toDto(savedUser)).thenReturn(resultUserDto);

        UserDto result = userService.addNewUser(inputUserDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("newUser", result.getUsername());
        assertEquals(UserRole.USER.getString(), result.getRole());

        verify(userRepository, times(1)).existsUserByUsername("newUser");
        verify(userMapper, times(1)).toEntity(inputUserDto);
        verify(passwordEncoder, times(1)).encode("rawPassword");
        verify(userRepository, times(1)).save(any(User.class));
        verify(userMapper, times(1)).toDto(savedUser);
    }

    @Test
    void addNewUserShouldThrowExceptionWhenUsernameAlreadyExists() {

        UserDto inputUserDto = new UserDto();
        inputUserDto.setUsername("existingUser");
        inputUserDto.setPassword("rawPassword");

        when(userRepository.existsUserByUsername("existingUser")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.addNewUser(inputUserDto));

        verify(userRepository, times(1)).existsUserByUsername("existingUser");
        verify(userMapper, never()).toEntity(any());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(userMapper, never()).toDto(any(User.class));
    }

    @Test
    void deleteUserShouldCallRepositoryDelete() {

        when(userMapper.toEntity(testUserDto)).thenReturn(testUser);
        doNothing().when(userRepository).delete(testUser);

        userService.deleteUser(testUserDto);

        verify(userMapper, times(1)).toEntity(testUserDto);
        verify(userRepository, times(1)).delete(testUser);
    }

    @Test
    void deleteUserByIdShouldCallRepositoryDeleteById() {
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUserById(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

}
