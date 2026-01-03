package com.graduationproject.power_fault_analysis.controller;

import com.graduationproject.power_fault_analysis.model.User;
import com.graduationproject.power_fault_analysis.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class HealthController {

    private final UserRepository userRepository;

    public HealthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/api/health")
    public Map<String, Object> health() {
        Map<String, Object> status = new HashMap<>();
        try {
            List<User> allUsers = userRepository.findAll();
            status.put("database", "connected");
            status.put("userCount", allUsers.size());
            status.put("users", allUsers.stream().map(User::getUsername).collect(Collectors.toList()));
            status.put("adminExists", userRepository.existsById("admin"));
        } catch (Exception e) {
            status.put("database", "error");
            status.put("message", e.getMessage());
        }
        return status;
    }
}