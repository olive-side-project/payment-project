package com.module.payment.controller;

import com.module.payment.annotation.Public;
import com.module.payment.auth.AuthenticationUser;
import com.module.payment.auth.JwtService;
import com.module.payment.auth.SessionService;
import com.module.payment.dto.LoginDto;
import com.module.payment.exception.PaymentException;
import com.module.payment.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
    public LoginDto.LoginResponse login(@RequestBody @Valid LoginDto.LoginRequest loginRequest) {
        AuthenticationUser authUser = authService.authenticateAndCreateSession(loginRequest);
        String jwt = jwtService.generateJwt(authUser.getUserSeq());
        sessionService.setAuthUser(jwt, authUser);

        return new LoginDto.LoginResponse(authUser.getUserSeq(), jwt);
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
    public LoginDto.SignUpResponse signup(@RequestBody @Valid LoginDto.SignUpRequest signUpRequest) {
        return authService.signup(signUpRequest);
    }
}
