package com.bytecrew.uscode.service;

import com.bytecrew.uscode.domain.User;
import com.bytecrew.uscode.dto.OcrExtractResponseDto;
import com.bytecrew.uscode.dto.UserSaveRequestDto;
import com.bytecrew.uscode.repository.UserRepository;
import com.bytecrew.uscode.util.AES256Util;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GoogleOcrService {

    private final UserRepository userRepository;

    @Value("${google.credentials.path}")
    private String credentialsPath;

    public OcrExtractResponseDto extractOnly(MultipartFile file) {
        try (ImageAnnotatorClient vision = createVisionClient()) {
            System.out.println(vision);
            ByteString imgBytes = ByteString.copyFrom(file.getBytes());
            System.out.println(imgBytes);

            Image img = Image.newBuilder().setContent(imgBytes).build();
            System.out.println(img);
            Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
            System.out.println(feat);
            AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                    .addFeatures(feat)
                    .setImage(img)
                    .build();
            System.out.println(request);

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
    private ImageAnnotatorClient createVisionClient() throws Exception {
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(credentialsPath));
        ImageAnnotatorSettings settings = ImageAnnotatorSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                .build();
        return ImageAnnotatorClient.create(settings);
    }
    private String extractJumin(String text) {
        return text.replaceAll("[^0-9-]", "")
                .replaceAll(".*?(\\d{6}-\\d{7}).*", "$1");
    }
}
