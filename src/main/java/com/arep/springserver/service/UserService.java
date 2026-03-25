package com.arep.springserver.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.arep.springserver.model.User;
import com.arep.springserver.repository.UserRepository;

@Service
public class UserService implements UserDetailsService {

    public record UserSummary(Long id, String username) {}

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(String username, String rawPassword) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Usuario ya existe");
        }
        User user = new User(username, passwordEncoder.encode(rawPassword));
        userRepository.save(user);
    }

    public boolean validateCredentials(String username, String rawPassword) {
        return userRepository.findByUsername(username)
            .map(user -> passwordEncoder.matches(rawPassword, user.getPassword()))
            .orElse(false);
    }

    public List<UserSummary> getAllUsers() {
        return userRepository.findAll().stream()
            .map(user -> new UserSummary(user.getId(), user.getUsername()))
            .toList();
    }

    public Optional<UserSummary> getUserById(Long id) {
        return userRepository.findById(id)
            .map(user -> new UserSummary(user.getId(), user.getUsername()));
    }

    public Optional<UserSummary> getProfile(String username) {
        return userRepository.findByUsername(username)
            .map(user -> new UserSummary(user.getId(), user.getUsername()));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        return org.springframework.security.core.userdetails.User
            .withUsername(user.getUsername())
            .password(user.getPassword())
            .roles("USER")
            .build();
    }
}
