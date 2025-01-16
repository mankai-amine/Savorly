package org.styd.intproj.savorly.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.styd.intproj.savorly.dto.ErrorResponse;
import org.styd.intproj.savorly.dto.JwtResponse;
import org.styd.intproj.savorly.dto.LoginRequest;
import org.styd.intproj.savorly.dto.RegisterResponse;
import org.styd.intproj.savorly.entity.User;
import org.styd.intproj.savorly.repository.UserRepository;
import org.styd.intproj.savorly.service.JwtTokenProvider;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;


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

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            String jwt = jwtTokenProvider.generateToken(authentication);
            return ResponseEntity.ok(new JwtResponse(jwt));

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping
    public ResponseEntity<?> getUserDetails(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String username = jwtTokenProvider.getUsernameFromToken(token);
            User user = userRepository.findByUsername(username);

            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

}
