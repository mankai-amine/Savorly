package org.styd.intproj.savorly.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private String code;
    private String message;
}
