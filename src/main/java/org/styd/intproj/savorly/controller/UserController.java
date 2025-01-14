package org.styd.intproj.savorly.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.styd.intproj.savorly.dto.ErrorResponse;
import org.styd.intproj.savorly.dto.RegisterResponse;
import org.styd.intproj.savorly.entity.User;
import org.styd.intproj.savorly.repository.UserRepository;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserRepository userRepository;


    @PostMapping("/register")
    public ResponseEntity<?> processRegister(@Valid @RequestBody User user) {
        try {
            if (!user.getPassword().equals(user.getPassword2())) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("passwordsDoNotMatch", "Passwords must match"));
            }

            if (userRepository.findByUsername(user.getUsername()) != null) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("usernameExists", "Username already exists"));
            }

            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);

            User savedUser = userRepository.save(user);

            return ResponseEntity.ok(new RegisterResponse("Registration successful", savedUser.getId()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("serverError", "Registration failed"));
        }
    }
}
