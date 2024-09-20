package com.module.user.controller;

import com.module.common.AuthenticationUser;
import com.module.common.annotation.Public;
import com.module.common.exception.PaymentException;
import com.module.user.auth.JwtService;
import com.module.user.auth.SessionService;
import com.module.user.dto.LoginDto;
import com.module.user.dto.LoginDto.LoginRequest;
import com.module.user.dto.LoginDto.LoginResponse;
import com.module.user.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
    public LoginResponse login(@RequestBody @Valid LoginRequest loginRequest) {
        AuthenticationUser authUser = authService.authenticateAndCreateSession(loginRequest);
        String jwt = jwtService.generateJwt(authUser.getUserSeq());
        sessionService.setAuthUser(jwt, authUser);

        return new LoginResponse(authUser.getUserSeq(), jwt);
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
