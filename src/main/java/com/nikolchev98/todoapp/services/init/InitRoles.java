package com.nikolchev98.todoapp.services.init;

import com.nikolchev98.todoapp.domain.entities.Role;
import com.nikolchev98.todoapp.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InitRoles implements CommandLineRunner {
    private final RoleRepository roleRepository;

    @Autowired
    public InitRoles(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        initializeRoles();
    }

    private void initializeRoles() {

        if (this.roleRepository.count() != 0) {
            return;
        }

        Role adminRole = new Role("ADMIN");
        Role userRole = new Role("USER");
        this.roleRepository.saveAll(List.of(adminRole, userRole));
    }
}
