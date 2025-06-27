package com.bytecrew.uscode.controller;


import com.bytecrew.uscode.dto.ReservationRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/reserve")
public class ReservationController {

    // [1] 농기구 이름 리스트 반환 (간단한 String 리스트)
    @GetMapping("/names/test")
    public ResponseEntity<List<String>> getToolNames() {
        List<String> toolNames = Arrays.asList("트랙터", "곡괭이");
        return ResponseEntity.ok(toolNames);
    }

    // [2] 농기구 예약 요청 받기
    @PostMapping("/test")
    public ResponseEntity<String> reserveTool(@RequestBody ReservationRequestDto request) {
        return ResponseEntity.ok("[" + request.getToolId() + "]번 농기구가 " + request.getUserName() + "님께 예약되었습니다.");
    }
}
