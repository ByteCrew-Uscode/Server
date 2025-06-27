package com.bytecrew.uscode.controller;

import com.bytecrew.uscode.domain.User;
import com.bytecrew.uscode.service.GoogleOcrService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ocr")
public class OcrController {

    private final GoogleOcrService googleOcrService;

    @PostMapping("/idcard")
    public ResponseEntity<User> uploadIdCard(@RequestParam MultipartFile image) {
        return ResponseEntity.ok(googleOcrService.extractAndSaveUser(image));
    }
}

