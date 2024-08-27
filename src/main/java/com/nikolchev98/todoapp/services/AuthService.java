package com.nikolchev98.todoapp.services;

import com.nikolchev98.todoapp.domain.dtos.RegisterDto;
import com.nikolchev98.todoapp.domain.entities.Role;
import com.nikolchev98.todoapp.domain.entities.UserEntity;
import com.nikolchev98.todoapp.repositories.RoleRepository;
import com.nikolchev98.todoapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    public ResponseEntity<String> register(RegisterDto registerDto) {

        if (this.userRepository.existsByUsername(registerDto.getUsername())) {
            return new ResponseEntity<>("Username is taken.", HttpStatus.BAD_REQUEST);
        }

        if (this.userRepository.existsByEmail(registerDto.getEmail())) {
            return new ResponseEntity<>("Email is taken.", HttpStatus.BAD_REQUEST);
        }

        Role role = this.roleRepository.findByName("USER").get();

        UserEntity user = new UserEntity();
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        user.setPassword(this.passwordEncoder.encode(registerDto.getPassword()));
        user.setRoles(Collections.singletonList(role));

        this.userRepository.save(user);
        return new ResponseEntity<>("Successfully registered new user.", HttpStatus.OK);
    }
}
