package com.student.studentservice.controller;

import com.student.studentservice.dto.*;
import com.student.studentservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;


    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO requestDTO) {
        log.info("POST /auth/login called for: {}", requestDTO.getEmail());
        LoginResponseDTO response = authService.userLogin(requestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponseDTO> refresh(@RequestBody RefreshTokenRequestDTO request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody RefreshTokenRequestDTO request) {
        return ResponseEntity.ok(authService.logout(request));
    }
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequestDTO request) {
        log.info("POST /auth/change-password called for: {}", request.getEmail());
        return ResponseEntity.ok(authService.changePassword(request));
    }
}