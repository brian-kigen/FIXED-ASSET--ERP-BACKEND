package com.emtech.Fixed.Assets.Emtech.Fixed.Assets.Controllers;

import com.emtech.Fixed.Assets.Emtech.Fixed.Assets.Config.JwtTokenService;
import com.emtech.Fixed.Assets.Emtech.Fixed.Assets.Entity.User;
import com.emtech.Fixed.Assets.Emtech.Fixed.Assets.Services.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.emtech.Fixed.Assets.Emtech.Fixed.Assets.Config.JwtAuthenticationFilter.logger;

@RestController
@Slf4j
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;


    @Autowired
    public UserController(UserService userService, PasswordEncoder passwordEncoder, JwtTokenService jwtTokenService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
//        user.setPassword(passwordEncoder.encode(user.getPassword())); // Ensure password is encoded
        User createdUser = userService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable Long userId, @RequestBody User user) {
        if (user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword())); // Ensure password is encoded
        }
        User updatedUser = userService.updateUser(userId, user);
        return ResponseEntity.ok(updatedUser);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok("User deleted successfully.");
    }

    //    @PostMapping("/register")
//    public ResponseEntity<String> registerUser(@RequestBody User user) {
//        if (userService.existsByUsername(user.getUsername())) {
//            return ResponseEntity.badRequest().body("Username is already taken.");
//        }
//        user.setPassword(passwordEncoder.encode(user.getPassword())); // Ensure password is encoded
//        userService.saveUser(user);
//        return ResponseEntity.ok("User registered successfully.");
//    }
    @PostMapping("/create-admin")
    public ResponseEntity<Object> createAdmin(@RequestBody User user) {
        try {
            if (userService.existsByUsername(user.getUsername())) {
                // If a user with the provided username already exists, return an appropriate error response
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Username already exists. Please choose a different username.");
            }

            // Attempt to create the admin user
            User createdAdmin = userService.createAdmin(user);

            // Return the created admin user in the response
            return ResponseEntity.ok(createdAdmin);
        } catch (Exception e) {
            // Log the exception
            logger.error("Error occurred while creating admin user: {}", e.getMessage());
            // Return an internal server error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String username, @RequestParam String newPassword) {
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found.");
        }
        user.setPassword(passwordEncoder.encode(newPassword)); // Ensure password is encoded
        userService.saveUser(user);
        return ResponseEntity.ok("Password reset successfully.");
    }

    //    @PostMapping("/login")
//    public ResponseEntity<Object> loginUser(@RequestBody User loginRequest) {
//        User user = userService.authenticateUser(loginRequest.getUsername(), loginRequest.getPassword());
//        log.info("Inside login");
//        System.out.println(user);
//        if (user != null) {
//            String token = jwtTokenService.generateToken(user.getUsername());
//            return ResponseEntity.ok(Collections.singletonMap("token", token));
//        }
//        return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Invalid username or password."));
//    }
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody User loginRequest) {
        User user = userService.authenticateUser(loginRequest.getUsername(), loginRequest.getPassword());
        if (user != null) {
            String token = jwtTokenService.generateToken(user.getUsername(), user.getUserRole().name());
            return ResponseEntity.ok(token);
        }
        return ResponseEntity.badRequest().body("Invalid username or password.");
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}