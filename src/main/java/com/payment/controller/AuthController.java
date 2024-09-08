package com.payment.controller;

import com.payment.dto.LoginDto.LoginRequest;
import com.payment.dto.LoginDto.LoginResponse;
import com.payment.dto.LoginDto.SignUpRequest;
import com.payment.dto.LoginDto.SignUpResponse;
import com.payment.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@Tag(name = "AUTH 컨트롤러")
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "로그인")
    public LoginResponse login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        return authService.login(loginRequest, request);
    }

    @PostMapping("/signup")
    @Operation(summary = "회원가입")
    public SignUpResponse signup(@RequestBody SignUpRequest signUpRequest) {
        return authService.signup(signUpRequest);
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃")
    public String logout(HttpSession session) {
        authService.logout(session);
        return "Logged out";
    }

    @GetMapping("/keep-alive")
    @Operation(summary = "세션 유지", description = "세션 만료 시간을 연장하여 세션을 유지")
    public ResponseEntity<String> keepAlive(HttpSession session) {
        authService.keepAlive(session);
        return ResponseEntity.ok("Session refreshed");
    }
}
