package com.nikolchev98.todoapp.web;

import com.nikolchev98.todoapp.domain.dtos.responses.AuthResponseDto;
import com.nikolchev98.todoapp.domain.dtos.imports.LoginFormDto;
import com.nikolchev98.todoapp.domain.dtos.imports.RegisterFormDto;
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

    @Autowired
    public AuthController(AuthService authService, AuthenticationManager authenticationManager, JWTGenerator jwtGenerator) {
        this.authService = authService;
    }


    @PostMapping("register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterFormDto registerFormDto,
                                           BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();

            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }

            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        return this.authService.register(registerFormDto);
    }

    @PostMapping("login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody @Valid LoginFormDto loginFormDto) {
        try {
            return new ResponseEntity<>(this.authService.login(loginFormDto), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}