package com.nikolchev98.todoapp.services;

import com.nikolchev98.todoapp.domain.dtos.imports.LoginFormDto;
import com.nikolchev98.todoapp.domain.dtos.imports.RegisterFormDto;
import com.nikolchev98.todoapp.domain.dtos.responses.AuthResponseDto;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
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

    @Test
    public void givenNonExistentUsername_login_shouldThrowUsernameNotFoundException() {
        //ARRANGE
        LoginFormDto loginFormDto = new LoginFormDto("nonExistentUser", "password");
        when(userRepository.findByUsername(loginFormDto.getUsername())).thenReturn(Optional.empty());

        //ACT & ASSERT
        assertThrows(UsernameNotFoundException.class, () -> authService.login(loginFormDto));
    }

    @Test
    public void givenIncorrectPassword_login_shouldThrowBadCredentialsException() {
        //ARRANGE
        LoginFormDto form = new LoginFormDto("existingUser", "wrongPassword");
        UserEntity user = new UserEntity();
        user.setPassword("encodedCorrectPassword");

        when(userRepository.findByUsername(form.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(form.getPassword(), user.getPassword())).thenReturn(false);

        //ACT & ASSERT
        assertThrows(BadCredentialsException.class, () -> authService.login(form));
    }

    @Test
    public void testLogin_Success() {
        //ARRANGE
        LoginFormDto loginFormDto = new LoginFormDto("existingUser", "correctPassword");

        UserEntity user = new UserEntity();
        user.setUsername(loginFormDto.getUsername());
        user.setPassword("encodedCorrectPassword");

        when(userRepository.findByUsername(loginFormDto.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginFormDto.getPassword(), user.getPassword())).thenReturn(true);

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtGenerator.generateToken(authentication)).thenReturn("generatedJwtToken");

        //ACT
        AuthResponseDto response = authService.login(loginFormDto);

        //ASSERT
        assertNotNull(response);
        assertEquals("generatedJwtToken", response.getToken());

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtGenerator, times(1)).generateToken(authentication);
        assertEquals(authentication, SecurityContextHolder.getContext().getAuthentication());
    }
}
