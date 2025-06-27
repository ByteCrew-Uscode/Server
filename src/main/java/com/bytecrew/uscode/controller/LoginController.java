package com.bytecrew.uscode.controller;

import com.bytecrew.uscode.dto.LoginRequest;
import com.bytecrew.uscode.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/login")
public class LoginController {

    private final LoginService loginService;

    @PostMapping
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        boolean success = loginService.login(loginRequest.getName(), loginRequest.getJumin());

        if (success) {
            return ResponseEntity.ok("로그인 성공");
        } else {
            return ResponseEntity.status(401).body("로그인 실패: 이름 또는 주민번호가 일치하지 않습니다.");
        }
    }
}

