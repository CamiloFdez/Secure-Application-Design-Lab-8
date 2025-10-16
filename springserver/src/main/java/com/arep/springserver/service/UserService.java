package com.arep.springserver.service;

import com.arep.springserver.model.User;
import com.arep.springserver.repository.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository repo;
    private final PasswordEncoder encoder;

    public UserService(UserRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    public void register(String username, String rawPassword) {
        if (repo.existsByUsername(username)) {
            throw new IllegalArgumentException("usuario ya existe");
        }
        repo.save(new User(username, encoder.encode(rawPassword)));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = repo.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("no existe"));
        return org.springframework.security.core.userdetails.User
            .withUsername(u.getUsername())
            .password(u.getPassword())
            .roles("USER")
            .build();
    }
}
