package com.example.lms_system_api.controller;

import com.example.lms_system_api.dto.LoginRequestDto;
import com.example.lms_system_api.dto.RefreshTokenRequest;
import com.example.lms_system_api.dto.TokenResponse;
import com.example.lms_system_api.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public TokenResponse login(@RequestBody LoginRequestDto request){
        return authService.login(request);
    }
    @PostMapping("/refresh")
    public TokenResponse refresh(@RequestBody RefreshTokenRequest request){
        return authService.refresh(request.getRefreshToken());
    }
}
