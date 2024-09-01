package com.nikolchev98.todoapp.web;

import com.nikolchev98.todoapp.domain.dtos.AuthResponseDto;
import com.nikolchev98.todoapp.domain.dtos.LoginDto;
import com.nikolchev98.todoapp.domain.dtos.RegisterDto;
import com.nikolchev98.todoapp.security.JWTGenerator;
import com.nikolchev98.todoapp.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthController(AuthService authService, AuthenticationManager authenticationManager, JWTGenerator jwtGenerator) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
    }


    @PostMapping("register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterDto registerDto,
                                           BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();

            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }

            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        return this.authService.register(registerDto);
    }

    @PostMapping("login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody @Valid LoginDto loginDto) {
        try {
            return new ResponseEntity<>(this.authService.login(loginDto), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}