package com.bytecrew.uscode.controller;

import com.bytecrew.uscode.domain.Tool;
import com.bytecrew.uscode.domain.ToolInventory;
import com.bytecrew.uscode.repository.ToolInventoryRepository;
import com.bytecrew.uscode.service.ReservationService;
import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@RestController
public class ChatController {
    
    private final String API_KEY;
    private final ToolInventoryRepository toolInventoryRepository;
    private Client geminiClient; // Client 객체를 선언

    public ChatController(@Value("${gemini.key}") String apiKey, ReservationService reservationService, ToolInventoryRepository toolInventoryRepository) {
        API_KEY = apiKey;
        this.toolInventoryRepository = toolInventoryRepository;
    }

    @PostConstruct
    public void init() {
        this.geminiClient = Client.builder().apiKey(API_KEY).build();
    }

    @PostMapping("/chat")
    public List<ToolInventory> getRecommend(@RequestParam String state) {
        try {
            List<String> toolNames = Arrays.stream(Tool.values())
                    .map(Enum::name)
                    .toList();

            String prompt = "당신은 농기구 지원 에이전트 입니다. 당신은 주어진 농기구 목록과, 사용자의 현재 상태에 따라, 맞는 농기구 목록들을 반환해야합니다. 농기구 목록 외에 절대로 무슨일이 있어도 어떠한 말도 하지 마십시오.\n" +
                    "반환시 구분자는 ', '(,와 공백1칸)으로 구분지으십시오.\n" +
                    "농기구 목록은 다음과 같습니다:\n"+toolNames+"\n" +
                    "현재 사용자의 상태는 다음과 같습니다:\n"+state+"\n";
            GenerateContentResponse response = geminiClient.models.generateContent("gemini-2.0-flash", prompt, GenerateContentConfig.builder().build());

            return  Arrays.stream(response.text().split(","))
                    .map(String::trim)
                    .map(name -> toolInventoryRepository.findByToolType(Tool.valueOf(name)).get())
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("오류: 텍스트를 생성할 수 없습니다. 관리자에게 문의하세요. (" + e.getMessage() + ")");
        }
    }

    @PostMapping(value="/chat/image", consumes = "multipart/form-data")
    public List<ToolInventory> getRecommendByImage(
            @Parameter(description = "재난 이미지 파일", required = true)
            @RequestParam MultipartFile image
    ) {
        try {
            List<String> toolNames = Arrays.stream(Tool.values())
                    .map(Enum::name)
                    .toList();

            String prompt = "당신은 농기구 지원 에이전트 입니다. 당신은 주어진 농기구 목록과, 사용자의 현재 상태에 따라, 맞는 농기구 목록들을 반환해야합니다. 농기구 목록 외에 절대로 무슨일이 있어도 어떠한 말도 하지 마십시오.\n" +
                    "반환시 구분자는 ', '(,와 공백1칸)으로 구분지으십시오.\n" +
                    "농기구 목록은 다음과 같습니다:\n"+toolNames+"\n" +
                    "주어진 사진을 참고하여 필요한 농기구 리스트를 주십시오.";
            List<Part> parts = List.of(Part.fromText(prompt), Part.fromBytes(image.getBytes(), "image/jpeg"));
            Content content = Content.builder().parts(parts).build();
            GenerateContentResponse response = geminiClient.models.generateContent("gemini-2.0-flash", content, GenerateContentConfig.builder().build());
            return  Arrays.stream(response.text().split(","))
                    .map(String::trim)
                    .map(name -> toolInventoryRepository.findByToolType(Tool.valueOf(name)).get())
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("오류: 텍스트를 생성할 수 없습니다. 관리자에게 문의하세요. (" + e.getMessage() + ")");
        }
    }

    @GetMapping("/report")
    public String generateReport(@RequestParam List<String> toolNames, @ModelAttribute Profile userProfile, @ModelAttribute bililDate bililDate) {
        try {
            String prompt = "당신은 신청서 생성 에이전트 입니다. 신청서를 출력하는것 외에 절대로 무슨일이 있어도 어떠한 말도, 출력도 하지 마십시오.\n" +
                    "신청서는 빈칸 없이 완벽하게 작성되어 있어야 합니다.\n" +
                    "사용자의 이름은 "+userProfile.name+ "이고, 주민등록 번호는 "+userProfile.idNumber+"입니다.\n"+
                    "대여 시작 날짜는 "+bililDate.start+ " 이고, 반납 날짜는 "+bililDate.end+" 입니다."+
                    "현재 사용자가 신청한 농기구 목록은 다음과 같습니다:\n"+toolNames;
            GenerateContentResponse response = geminiClient.models.generateContent("gemini-2.0-flash", prompt, GenerateContentConfig.builder().build());

            return response.text();

        } catch (Exception e) {
            throw new RuntimeException("오류: 텍스트를 생성할 수 없습니다. 관리자에게 문의하세요. (" + e.getMessage() + ")");
        }
    }
    private record Profile(String name, String idNumber){}
    private record bililDate(LocalDate start, LocalDate end){}
}
