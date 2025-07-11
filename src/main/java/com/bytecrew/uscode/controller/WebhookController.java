package com.bytecrew.uscode.controller;

import com.bytecrew.uscode.domain.Tool;
import com.bytecrew.uscode.dto.ReservationRequestDto;
import com.bytecrew.uscode.service.ReservationService;
import com.bytecrew.uscode.service.ToolLocationService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dialogflow")
@RequiredArgsConstructor
public class WebhookController {

    private final ReservationService reservationService;
    private final ToolLocationService toolLocationService;

    @PostMapping(value = "/webhook", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Hidden
    public ResponseEntity<Map<String, Object>> dialogflowWebhook(@RequestBody Map<String, Object> body) {
        // 1. 인텐트 이름 파싱
        String intentName = ((Map<String, Object>) body.get("queryResult"))
                .get("intent") instanceof Map intent
                ? (String) intent.get("displayName")
                : null;

        // 2. 파라미터 추출
        Map<String, Object> parameters = (Map<String, Object>) ((Map<String, Object>) body.get("queryResult")).get("parameters");
        String responseText;

        switch (intentName) {
            case "ListToolsIntent":
                responseText = handleListToolsIntent();
                break;
            case "ReserveToolIntent":
                responseText = handleReserveToolIntent(parameters);
                break;
            default:
                responseText = "죄송합니다. 이해하지 못했습니다.";
        }

        // 3. Dialogflow 응답 포맷
        Map<String, Object> response = new HashMap<>();
        response.put("fulfillmentText", responseText);
        return ResponseEntity.ok(response);
    }

    private String handleListToolsIntent() {
        List<Tool> toolNames = reservationService.getAllTools().stream().limit(5).toList();;
        String sum = toolNames.stream()
                .map(Tool::name)  // enum 이름을 String으로 변환
                .collect(Collectors.joining(", ")); // 원하는 구분자

        return "현재 " + sum +" 등의 농기구 대여가 가능합니다. 무엇을 대여 하시겠습니까?";
    }

    private String handleReserveToolIntent(Map<String, Object> params) {
        try {
            List<String> toolNames = ((List<String>) params.get("tool"));
            String start = (String) params.get("startDate");
            String end = (String) params.get("endDate");

            if (toolNames == null || start == null || end == null) {
                return "도구 이름, 대여 시작 날짜, 대여 종료 날짜를 모두 알려주세요.";
            }

            // 날짜 및 시간 파싱
            LocalDate startDate = LocalDate.parse(start.substring(0, 10)); // "2025-06-28" 형식
            LocalDate endDate = LocalDate.parse(end.substring(0, 10));

            //가장 가까운 대여소
            String nearest = "";
            for(String toolName : toolNames) {
                nearest = toolLocationService.getLocationsByTool(Tool.valueOf(toolName)).getFirst().location();

                // 예약 생성
                ReservationRequestDto dto = new ReservationRequestDto();
                dto.setTool(Tool.valueOf(toolName));
                dto.setStartDate(startDate);
                dto.setEndDate(endDate);
                dto.setLocation(nearest);
                reservationService.createReservation(dto);
            }

            return String.format("%s를 %s 부터 %s 까지 예약 신청이 완료됐습니다. 대여소는 %s 입니다. 계약서 작성을 해드릴까요?", toolNames, startDate, endDate, nearest);
        } catch (Exception e) {
            return "예약 처리 중 오류가 발생했습니다. 정확한 날짜와 시간을 다시 말해주세요.";
        }
    }
}
