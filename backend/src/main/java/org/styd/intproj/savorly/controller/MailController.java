//package org.styd.intproj.savorly.controller;
//
//import jakarta.mail.MessagingException;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.*;
//import org.styd.intproj.savorly.dto.MailRequest;
//import org.styd.intproj.savorly.service.MailService;
//
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/mail")
//public class MailController {
//
//    private final MailService mailService;
//
//    public MailController(MailService mailService) {
//        this.mailService = mailService;
//    }
//
//    @PostMapping("/send")
//    public ResponseEntity<String> sendMail(@Validated @RequestBody MailRequest mailRequest) {
//        try {
//            String to = mailRequest.getTo();
//            String subject = mailRequest.getSubject();
//            String content = mailRequest.getMessage();
//            String name = mailRequest.getName();
//            boolean isHtml = mailRequest.isHtml();
//
//            mailService.sendEmail(to, subject, content,name, isHtml);
//            return ResponseEntity.ok("Email sent successfully!");
//
//        } catch (MessagingException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending email: " + e.getMessage());
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request: " + e.getMessage());
//        }
//    }
//}
