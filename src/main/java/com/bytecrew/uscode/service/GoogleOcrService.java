package com.bytecrew.uscode.service;

import com.bytecrew.uscode.domain.User;
import com.bytecrew.uscode.repository.UserRepository;
import com.bytecrew.uscode.util.AES256Util;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.protobuf.ByteString;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoogleOcrService {

    private final UserRepository userRepository;

    public User extractAndSaveUser(MultipartFile file) {
        try (ImageAnnotatorClient vision = ImageAnnotatorClient.create()) {

            // 1. 이미지 파일을 바이트로 읽고 ByteString으로 변환
            ByteString imgBytes = ByteString.copyFrom(file.getBytes());

            // 2. OCR 요청 구성
            Image img = Image.newBuilder().setContent(imgBytes).build();
            Feature feat = Feature.newBuilder().setType(Type.TEXT_DETECTION).build();
            AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                    .addFeatures(feat)
                    .setImage(img)
                    .build();

            // 3. Vision API 호출
            BatchAnnotateImagesResponse batchResponse = vision.batchAnnotateImages(List.of(request));
            AnnotateImageResponse response = batchResponse.getResponsesList().get(0);

            if (response.hasError()) {
                throw new RuntimeException("OCR 오류: " + response.getError().getMessage());
            }

            // 4. 텍스트 추출
            String text = response.getFullTextAnnotation().getText();

            String name = extractName(text);
            String jumin = extractJumin(text);

            String encryptedJumin = AES256Util.encrypt(jumin);

            // 5. DB 저장
            User user = User.builder()
                    .name(name)
                    .encryptedJumin(encryptedJumin)
                    .build();

            return userRepository.save(user);

        } catch (Exception e) {
            throw new RuntimeException("OCR 처리 실패", e);
        }
    }

    private String extractName(String text) {
        for (String line : text.split("\\n")) {
            if (line.contains("성명")) {
                return line.replace("성명", "").trim();
            }
        }
        return "이름없음";
    }

    private String extractJumin(String text) {
        return text.replaceAll("[^0-9-]", "")
                .replaceAll(".*?(\\d{6}-\\d{7}).*", "$1");
    }
}
