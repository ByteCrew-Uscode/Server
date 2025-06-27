package com.bytecrew.uscode.service;

import com.bytecrew.uscode.domain.User;
import com.bytecrew.uscode.dto.OcrExtractResponseDto;
import com.bytecrew.uscode.dto.UserSaveRequestDto;
import com.bytecrew.uscode.repository.UserRepository;
import com.bytecrew.uscode.util.AES256Util;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoogleOcrService {

    private final UserRepository userRepository;

    public OcrExtractResponseDto extractOnly(MultipartFile file) {
        try (ImageAnnotatorClient vision = ImageAnnotatorClient.create()) {
            ByteString imgBytes = ByteString.copyFrom(file.getBytes());

            Image img = Image.newBuilder().setContent(imgBytes).build();
            Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
            AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                    .addFeatures(feat)
                    .setImage(img)
                    .build();

            AnnotateImageResponse response = vision.batchAnnotateImages(List.of(request))
                    .getResponsesList().get(0);

            if (response.hasError()) {
                throw new RuntimeException("OCR 오류: " + response.getError().getMessage());
            }

            String text = response.getFullTextAnnotation().getText();
            String name = extractName(text);
            String jumin = extractJumin(text);

            return new OcrExtractResponseDto(name, jumin);

        } catch (Exception e) {
            throw new RuntimeException("OCR 처리 실패", e);
        }
    }

    public User saveConfirmedUser(UserSaveRequestDto dto) {
        String encryptedJumin = AES256Util.encrypt(dto.getJumin());
        User user = User.builder()
                .name(dto.getName())
                .encryptedJumin(encryptedJumin)
                .build();
        return userRepository.save(user);
    }

    private String extractName(String text) {
        String[] lines = text.split("\\n");
        for (String line : lines) {
            if (line.trim().equals("주민등록증")) continue;
            if (line.matches("^[가-힣]{2,4}$")) return line.trim();
        }
        return "이름없음";
    }

    private String extractJumin(String text) {
        return text.replaceAll("[^0-9-]", "")
                .replaceAll(".*?(\\d{6}-\\d{7}).*", "$1");
    }
}
