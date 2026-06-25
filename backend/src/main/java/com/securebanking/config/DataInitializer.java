package com.securebanking.config;

import com.securebanking.entity.Role;
import com.securebanking.entity.User;
import com.securebanking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User("admin", passwordEncoder.encode("admin123"),
                    Role.ROLE_ADMIN, "System Administrator", "ADMIN-001");
            userRepository.save(admin);
            System.out.println("✅ Admin user created: admin / admin123");
        }

        if (!userRepository.existsByUsername("user1")) {
            User user1 = new User("user1", passwordEncoder.encode("user123"),
                    Role.ROLE_USER, "Nguyen Van A", "VCB-1001-2024");
            userRepository.save(user1);
            System.out.println("✅ User created: user1 / user123");
        }

        if (!userRepository.existsByUsername("user2")) {
            User user2 = new User("user2", passwordEncoder.encode("user123"),
                    Role.ROLE_USER, "Tran Thi B", "VCB-1002-2024");
            userRepository.save(user2);
            System.out.println("✅ User created: user2 / user123");
        }
    }
}
