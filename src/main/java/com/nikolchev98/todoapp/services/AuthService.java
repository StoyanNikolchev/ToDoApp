package com.nikolchev98.todoapp.services;

import com.nikolchev98.todoapp.domain.dtos.RegisterDto;
import com.nikolchev98.todoapp.domain.entities.UserEntity;
import com.nikolchev98.todoapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<String> register(RegisterDto registerDto) {

        if (this.userRepository.existsByUsername(registerDto.getUsername())) {
            return new ResponseEntity<>("Username is taken!", HttpStatus.BAD_REQUEST);
        }

        UserEntity user = new UserEntity();
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        user.setPassword(this.passwordEncoder.encode(registerDto.getPassword()));

        this.userRepository.save(user);
        return new ResponseEntity<>("Successfully registered new user", HttpStatus.OK);
    }
}
