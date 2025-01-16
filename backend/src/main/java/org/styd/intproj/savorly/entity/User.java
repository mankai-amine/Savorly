package org.styd.intproj.savorly.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users") // maybe public.users?
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message="Username is required")
    @Column(unique = true)
    @Size(min = 3, max = 30, message = "Username must contain between {min} and {max} characters")
    private String username;

    @NotBlank(message="Password is required")
    @Size(min = 6, max = 100, message = "Password must contain between {min} and {max} characters")
    private String password;

    @Transient
    private String password2;
}
