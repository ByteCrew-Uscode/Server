package com.bytecrew.uscode.controller;

import com.bytecrew.uscode.domain.User;
import com.bytecrew.uscode.dto.OcrExtractResponseDto;
import com.bytecrew.uscode.dto.UserSaveRequestDto;
import com.bytecrew.uscode.service.GoogleOcrService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ocr")
@Tag(name = "OCR", description = "OCR 기반 주민등록증 정보 추출 및 저장")
public class GoogleOcrController {

    private final GoogleOcrService googleOcrService;

    @Operation(
            summary = "주민등록증 OCR 추출",
            description = "이미지에서 이름과 주민번호를 추출합니다. DB에 저장하지 않고 응답만 반환합니다."
    )
    @PostMapping("/idcard")
    public ResponseEntity<OcrExtractResponseDto> extractFromIdCard(
            @Parameter(description = "주민등록증 이미지 파일", required = true)
            @RequestParam MultipartFile image
    ) {
        return ResponseEntity.ok(googleOcrService.extractOnly(image));
    }

    @Operation(
            summary = "OCR 추출값 확정 및 저장",
            description = "OCR 결과를 확인 및 수정 후 최종 저장합니다."
    )
    @PostMapping("/confirm")
    public ResponseEntity<User> saveConfirmed(
            @RequestBody UserSaveRequestDto dto
    ) {
        return ResponseEntity.ok(googleOcrService.saveConfirmedUser(dto));
    }
}
