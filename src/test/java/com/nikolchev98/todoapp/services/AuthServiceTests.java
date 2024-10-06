package com.nikolchev98.todoapp.services;

import com.nikolchev98.todoapp.domain.dtos.imports.RegisterFormDto;
import com.nikolchev98.todoapp.domain.dtos.responses.RegisterResponseDto;
import com.nikolchev98.todoapp.domain.entities.Role;
import com.nikolchev98.todoapp.domain.entities.UserEntity;
import com.nikolchev98.todoapp.repositories.RoleRepository;
import com.nikolchev98.todoapp.repositories.UserRepository;
import com.nikolchev98.todoapp.security.JWTGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

public class AuthServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JWTGenerator jwtGenerator;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    public void setUp() {
        openMocks(this);
    }

    @Test
    public void givenRegistrationFormWithExistingUsername_register_shouldReturnBadRequest() {
        //ARRANGE
        RegisterFormDto registerFormDto = new RegisterFormDto();
        registerFormDto.setUsername("Test");

        when(userRepository.existsByUsername("Test")).thenReturn(true);

        //ACT
        ResponseEntity<?> response = authService.register(registerFormDto);

        //ASSERT
        Map<String, String> errors = (Map<String, String>) response.getBody();
        assertEquals("Username is taken.", errors.get("username"));
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void givenRegistrationFormWithExistingEmail_register_shouldReturnBadRequest() {
        //ARRANGE
        RegisterFormDto registerFormDto = new RegisterFormDto();
        registerFormDto.setEmail("test@test.com");

        when(userRepository.existsByEmail("test@test.com")).thenReturn(true);

        //ACT
        ResponseEntity<?> response = authService.register(registerFormDto);

        //ASSERT
        Map<String, String> errors = (Map<String, String>) response.getBody();
        assertEquals("Email is taken.", errors.get("email"));
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void successfullyRegisteringUser_register_shouldSaveToDatabaseAndReturnRegisterResponseDto() {
        //ARRANGE
        RegisterFormDto registerFormDto = new RegisterFormDto();
        registerFormDto.setEmail("test@test.com");
        registerFormDto.setUsername("Test");
        registerFormDto.setPassword("password");
        registerFormDto.setConfirmPassword("password");

        when(userRepository.existsByUsername("Test")).thenReturn(false);
        when(userRepository.existsByEmail("test@test.com")).thenReturn(false);

        Role role = new Role();
        role.setName("USER");
        role.setId(UUID.randomUUID());
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(role));

        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        //ACT
        ResponseEntity<?> response = authService.register(registerFormDto);

        //ASSERT
        ArgumentCaptor<UserEntity> userEntityCaptor = ArgumentCaptor.forClass(UserEntity.class);

        verify(userRepository).save(userEntityCaptor.capture());
        UserEntity capturedEntity = userEntityCaptor.getValue();

        assertEquals("Test", capturedEntity.getUsername());
        assertEquals("encodedPassword", capturedEntity.getPassword());
        assertEquals("test@test.com", capturedEntity.getEmail());
        assertEquals("USER", capturedEntity.getRoles().get(0).getName());


        RegisterResponseDto registerResponseDto = (RegisterResponseDto) response.getBody();
        assertEquals("Test", registerResponseDto.getUsername());
        assertEquals("test@test.com", registerResponseDto.getEmail());

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
