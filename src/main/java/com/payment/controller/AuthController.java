package com.payment.controller;

import com.payment.annotation.Public;
import com.payment.auth.AuthenticationUser;
import com.payment.auth.JwtService;
import com.payment.auth.SessionService;
import com.payment.dto.LoginDto.LoginRequest;
import com.payment.dto.LoginDto.LoginResponse;
import com.payment.dto.LoginDto.SignUpRequest;
import com.payment.dto.LoginDto.SignUpResponse;
import com.payment.exception.PaymentException;
import com.payment.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@Tag(name = "인증 API")
public class AuthController {
    private final AuthService authService;
    private final SessionService sessionService;
    private final JwtService jwtService;

    @Public
    @PostMapping("/login")
    @Operation(summary = "로그인")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        AuthenticationUser authUser = authService.authenticateAndCreateSession(loginRequest);
        String jwt = jwtService.generateJwt(authUser.getUserSeq());
        sessionService.setAuthUser(jwt, authUser);

        return new LoginResponse(authUser.getUserSeq(), authUser.getEmail());
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃")
    public void logout(HttpServletRequest request) {
        String token = (String) request.getAttribute("authToken");
        if (token != null) {
            sessionService.removeAuthUser(token);
        } else {
            throw new PaymentException("NO_TOKEN_FOUND", "No token found in request.", HttpStatus.UNAUTHORIZED);
        }
    }

    @Public
    @PostMapping("/signup")
    @Operation(summary = "회원가입")
    public SignUpResponse signup(@RequestBody SignUpRequest signUpRequest) {
        return authService.signup(signUpRequest);
    }
}
