package com.nikolchev98.todoapp.services;

import com.nikolchev98.todoapp.domain.dtos.LoginDto;
import com.nikolchev98.todoapp.domain.dtos.RegisterDto;
import com.nikolchev98.todoapp.domain.entities.Role;
import com.nikolchev98.todoapp.domain.entities.UserEntity;
import com.nikolchev98.todoapp.repositories.RoleRepository;
import com.nikolchev98.todoapp.repositories.UserRepository;
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

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
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

    public void login(LoginDto loginDto) {
        UserEntity user = this.userRepository
                .findByUsername(loginDto.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        if (!this.passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Incorrect username or password.");
        }

        Authentication authentication = this.authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
