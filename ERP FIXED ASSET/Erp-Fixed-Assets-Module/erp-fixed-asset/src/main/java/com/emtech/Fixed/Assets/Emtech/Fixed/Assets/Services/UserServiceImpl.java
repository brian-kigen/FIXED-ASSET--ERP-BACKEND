package com.emtech.Fixed.Assets.Emtech.Fixed.Assets.Services;

import com.emtech.Fixed.Assets.Emtech.Fixed.Assets.Entity.User;
import com.emtech.Fixed.Assets.Emtech.Fixed.Assets.Repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @PreAuthorize("hasRole('ADMIN')")
    public User createUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists: " + user.getUsername());
        }
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (user.getUserRole() == null) {
            user.setUserRole(User.UserRole.OFFICER);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User updateUser(Long userId, User updatedUser) {
        // Find existing user
        User existingUser = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Update fields
        if (updatedUser.getPassword() != null) {
            existingUser.setPassword(updatedUser.getPassword()); // Assuming password is already encoded
        }
        if (updatedUser.getEmail() != null) {
            existingUser.setEmail(updatedUser.getEmail());
        }
        if (updatedUser.getFirstName() != null) {
            existingUser.setFirstName(updatedUser.getFirstName());
        }
        if (updatedUser.getLastName() != null) {
            existingUser.setLastName(updatedUser.getLastName());
        }
        if (updatedUser.getBranch() != null) {
            existingUser.setBranch(updatedUser.getBranch());
        }

        // Save updated user
        return userRepository.save(existingUser);
    }


    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            userRepository.deleteById(userId);
        } else {
            throw new IllegalArgumentException("User not found with id: " + userId);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'OFFICER')")
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'OFFICER')")
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @PreAuthorize("permitAll()")
    public User authenticateUser(String username, String password) {
        User user = userRepository.findByUsername(username).orElse(null);
        log.info("User is " + user);
//        System.out.println(user);
        if (user != null) {
            System.out.println("verifying password");
            System.out.println(password);
            System.out.println(user.getPassword());

            if (passwordEncoder.matches(password, user.getPassword())) {
                log.info("Inside authenticate user");
                return user;

            }
        }
        return null;
    }

    public User createAdmin(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setUserRole(User.UserRole.ADMIN);
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();

    }
}

