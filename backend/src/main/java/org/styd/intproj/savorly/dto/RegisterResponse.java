package org.styd.intproj.savorly.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterResponse {
    private String message;
    private Long userId;
}
