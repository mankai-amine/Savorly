package org.styd.intproj.savorly.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MailRequest {
    private String to;
    private String subject;
    private String message;
    private String name;
    private boolean isHtml;

}
