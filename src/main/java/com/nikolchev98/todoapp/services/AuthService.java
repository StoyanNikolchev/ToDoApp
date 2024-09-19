package com.nikolchev98.todoapp.services;

import com.nikolchev98.todoapp.domain.dtos.responses.AuthResponseDto;
import com.nikolchev98.todoapp.domain.dtos.imports.LoginFormDto;
import com.nikolchev98.todoapp.domain.dtos.imports.RegisterFormDto;
import com.nikolchev98.todoapp.domain.dtos.responses.RegisterResponseDto;
import com.nikolchev98.todoapp.domain.entities.Role;
import com.nikolchev98.todoapp.domain.entities.UserEntity;
import com.nikolchev98.todoapp.repositories.RoleRepository;
import com.nikolchev98.todoapp.repositories.UserRepository;
import com.nikolchev98.todoapp.security.JWTGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final JWTGenerator jwtGenerator;

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository, AuthenticationManager authenticationManager, JWTGenerator jwtGenerator) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
        this.jwtGenerator = jwtGenerator;
    }

    public ResponseEntity<?> register(RegisterFormDto registerFormDto) {
        Map<String, String> errors = new HashMap<>();

        if (this.userRepository.existsByUsername(registerFormDto.getUsername())) {
            errors.put("username", "Username is taken.");
        }

        if (this.userRepository.existsByEmail(registerFormDto.getEmail())) {
            errors.put("email", "Email is taken.");
        }

        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        Role role = this.roleRepository.findByName("USER").get();

        UserEntity user = new UserEntity();
        user.setUsername(registerFormDto.getUsername());
        user.setEmail(registerFormDto.getEmail());
        user.setPassword(this.passwordEncoder.encode(registerFormDto.getPassword()));
        user.setRoles(Collections.singletonList(role));

        this.userRepository.save(user);
        return new ResponseEntity<>(new RegisterResponseDto(registerFormDto.getUsername(), registerFormDto.getEmail()), HttpStatus.OK);
    }

    public AuthResponseDto login(LoginFormDto loginFormDto) {
        UserEntity user = this.userRepository
                .findByUsername(loginFormDto.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        if (!this.passwordEncoder.matches(loginFormDto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Incorrect username or password.");
        }

        Authentication authentication = this.authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginFormDto.getUsername(), loginFormDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtGenerator.generateToken(authentication);
        return new AuthResponseDto(token);
    }
}
