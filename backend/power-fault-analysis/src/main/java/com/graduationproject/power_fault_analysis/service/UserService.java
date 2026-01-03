package com.graduationproject.power_fault_analysis.service;

import com.graduationproject.power_fault_analysis.model.User;
import com.graduationproject.power_fault_analysis.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements CommandLineRunner {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void run(String... args) {
        System.out.println(">>> [INIT] Starting UserService Initialization...");
        try {
            // Check if admin exists
            boolean exists = userRepository.existsById("admin");
            if (!exists) {
                User admin = new User("admin", "admin123", "ADMIN");
                userRepository.save(admin);
                System.out.println(">>> [INIT] Created default admin user: admin / admin123");
            } else {
                // Ensure password is correct even if it exists
                User admin = userRepository.findById("admin").get();
                admin.setPassword("admin123");
                userRepository.save(admin);
                System.out.println(">>> [INIT] Admin user already exists. Password verified/reset to admin123.");
            }
        } catch (Exception e) {
            System.err.println(">>> [INIT] CRITICAL ERROR: Could not connect to Neo4j to initialize users!");
            System.err.println(">>> [INIT] Error Details: " + e.getMessage());
            // Don't throw, let app start so user can see health check
        }
    }

    public User login(String username, String password) {
        if (username == null || password == null) return null;
        
        String cleanUsername = username.trim();
        System.out.println(">>> [LOGIN] Attempt: " + cleanUsername);
        
        try {
            Optional<User> userOpt = userRepository.findById(cleanUsername);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                if (user.getPassword().equals(password)) {
                    return user;
                } else {
                    System.out.println(">>> [LOGIN] Fail: Password mismatch for " + cleanUsername);
                }
            } else {
                System.out.println(">>> [LOGIN] Fail: User not found: " + cleanUsername);
            }
        } catch (Exception e) {
            System.err.println(">>> [LOGIN] DB ERROR: " + e.getMessage());
        }
        return null;
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(String username) {
        userRepository.deleteById(username);
    }

    public User updateUser(String username, User userDetails) {
        User user = userRepository.findById(username).orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(userDetails.getPassword());
        user.setRole(userDetails.getRole());
        return userRepository.save(user);
    }
}