package com.graduationproject.power_fault_analysis.service;

import com.graduationproject.power_fault_analysis.model.User;
import com.graduationproject.power_fault_analysis.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements CommandLineRunner {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        System.out.println(">>> [INIT] Starting UserService Initialization...");
        try {
            String adminUser = "admin";
            String adminPass = "admin123";
            String hashedPass = hashPassword(adminPass);
            
            Optional<User> existingAdmin = userRepository.findById(adminUser);
            
            if (existingAdmin.isEmpty()) {
                User admin = new User(adminUser, hashedPass, "ADMIN");
                userRepository.save(admin);
                System.out.println(">>> [INIT] Created default admin user: admin / " + adminPass);
            } else {
                // Always reset admin password to ensure access if hashing logic changes
                User admin = existingAdmin.get();
                if (!admin.getPassword().equals(hashedPass)) {
                    admin.setPassword(hashedPass);
                    userRepository.save(admin);
                    System.out.println(">>> [INIT] Admin password updated/reset to default.");
                }
            }
        } catch (Exception e) {
            System.err.println(">>> [INIT] CRITICAL ERROR: Could not connect to Neo4j to initialize users!");
            System.err.println(">>> [INIT] Error Details: " + e.getMessage());
        }
    }

    public User login(String username, String password) {
        if (username == null || password == null) return null;
        
        String cleanUsername = username.trim();
        // System.out.println(">>> [LOGIN] Attempt: " + cleanUsername);
        
        try {
            Optional<User> userOpt = userRepository.findById(cleanUsername);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                String hashedInput = hashPassword(password);
                if (user.getPassword().equals(hashedInput)) {
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
        // Hash password before saving
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(hashPassword(user.getPassword()));
        }
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
        
        // Only update password if a new one is provided
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
             user.setPassword(hashPassword(userDetails.getPassword()));
        }
        
        user.setRole(userDetails.getRole());
        return userRepository.save(user);
    }
    
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
            for (int i = 0; i < encodedhash.length; i++) {
                String hex = Integer.toHexString(0xff & encodedhash[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
}
