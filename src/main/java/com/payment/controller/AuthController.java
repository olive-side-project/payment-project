package com.payment.controller;

import com.payment.dto.LoginDto.LoginRequest;
import com.payment.dto.LoginDto.LoginResponse;
import com.payment.dto.LoginDto.SignUpRequest;
import com.payment.dto.LoginDto.SignUpResponse;
import com.payment.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest, HttpSession session) {
        return authService.login(loginRequest, session);
    }

    @PostMapping("/signup")
    public SignUpResponse signup(@RequestBody SignUpRequest signUpRequest) {
        return authService.signup(signUpRequest);
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        authService.logout(session);
        return "Logged out";
    }

    @GetMapping("/keep-alive")
    public ResponseEntity<String> keepAlive(HttpSession session) {
        authService.keepAlive(session);
        return ResponseEntity.ok("Session refreshed");
    }
}
