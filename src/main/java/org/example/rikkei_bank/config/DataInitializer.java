package org.example.rikkei_bank.config;

import org.example.rikkei_bank.entity.Role;
import org.example.rikkei_bank.entity.User;
import org.example.rikkei_bank.repository.RoleRepository;
import org.example.rikkei_bank.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Tạo Roles
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .name("ADMIN").description("Quản trị viên").build()));

        Role staffRole = roleRepository.findByName("STAFF")
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .name("STAFF").description("Nhân viên").build()));

        Role customerRole = roleRepository.findByName("CUSTOMER")
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .name("CUSTOMER").description("Khách hàng").build()));

        // Tạo Admin mặc định
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .fullName("Administrator")
                    .email("admin@rikkei.bank")
                    .isActive(true)
                    .isKyc(true)
                    .build();

            admin.setRoles(Set.of(adminRole));
            userRepository.save(admin);
            System.out.println("✅ Admin account created: username=admin, password=admin123");
        }
    }
}