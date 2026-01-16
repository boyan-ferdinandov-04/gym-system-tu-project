package com.example.gym_management.config;

import com.example.gym_management.entity.Gym;
import com.example.gym_management.entity.User;
import com.example.gym_management.entity.UserRole;
import com.example.gym_management.repository.GymRepository;
import com.example.gym_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("local")
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final GymRepository gymRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Create default gym if not exists
        Gym gym = gymRepository.findByName("Main Gym").orElseGet(() -> {
            Gym newGym = new Gym("Main Gym", "123 Fitness Street", "555-0100");
            return gymRepository.save(newGym);
        });

        // Create admin user if not exists
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User(
                    "admin",
                    passwordEncoder.encode("admin123"),
                    "admin@gym.com",
                    "Admin",
                    "User",
                    UserRole.ADMIN,
                    null
            );
            userRepository.save(admin);
            System.out.println(">>> Created default admin user: admin / admin123");
        }

        // Create manager user if not exists
        if (!userRepository.existsByUsername("manager")) {
            User manager = new User(
                    "manager",
                    passwordEncoder.encode("manager123"),
                    "manager@gym.com",
                    "Manager",
                    "User",
                    UserRole.MANAGER,
                    gym
            );
            userRepository.save(manager);
            System.out.println(">>> Created default manager user: manager / manager123");
        }

        // Create employee user if not exists
        if (!userRepository.existsByUsername("employee")) {
            User employee = new User(
                    "employee",
                    passwordEncoder.encode("employee123"),
                    "employee@gym.com",
                    "Employee",
                    "User",
                    UserRole.EMPLOYEE,
                    gym
            );
            userRepository.save(employee);
            System.out.println(">>> Created default employee user: employee / employee123");
        }
    }
}
