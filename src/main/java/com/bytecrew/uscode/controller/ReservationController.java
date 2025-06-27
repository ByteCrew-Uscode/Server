package com.bytecrew.uscode.controller;


import com.bytecrew.uscode.domain.Reservation;
import com.bytecrew.uscode.dto.ReservationRequestDto;
import com.bytecrew.uscode.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/reserve")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

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
    // 생성
    @PostMapping
    public ResponseEntity<Reservation> createReservation(@RequestBody Reservation reservation) {
        return ResponseEntity.ok(reservationService.createReservation(reservation));
    }

    // 전체 조회
    @GetMapping
    public ResponseEntity<List<Reservation>> getAllReservations() {
        return ResponseEntity.ok(reservationService.getAllReservations());
    }

    // 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable Long id) {
        return reservationService.getReservationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 수정
    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(@PathVariable Long id, @RequestBody Reservation reservation) {
        return ResponseEntity.ok(reservationService.updateReservation(id, reservation));
    }

    // 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }
}
