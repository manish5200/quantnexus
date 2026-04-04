package com.quantnexus.config;

import com.quantnexus.domain.User;
import com.quantnexus.domain.enums.Role;
import com.quantnexus.domain.enums.UserStatus;
import com.quantnexus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Override
    public void run(String @NonNull ... args) throws Exception {
        // Check if the admin already exists using the injected email
        if(!userRepository.existsByEmail(adminEmail)){
            log.info("System Seed: Provisioning default Admin account...");
            User admin = User.builder()
                    .email(adminEmail)
                    .fullName("System Administrator")
                    .passwordHash(passwordEncoder.encode(adminPassword))
                    .role(Role.ADMIN)
                    .userStatus(UserStatus.ACTIVE)
                    .failedLoginAttempts(0)
                    .build();
            userRepository.save(admin);
            log.info("System Seed: Admin account created successfully [{}]", adminEmail);
        }else{
            log.info("Admin already exists [{}]", adminEmail);
        }
    }
}
