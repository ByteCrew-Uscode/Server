package com.bytecrew.uscode.controller;

import com.bytecrew.uscode.domain.Tool;
import com.bytecrew.uscode.domain.Reservation;
import com.bytecrew.uscode.domain.ToolInventory;
import com.bytecrew.uscode.dto.ReservationRequestDto;
import com.bytecrew.uscode.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reserve")
@RequiredArgsConstructor
@Tag(name = "Reservation", description = "농기구 예약 관련 API")
public class ReservationController {

    private final ReservationService reservationService;

    @Operation(summary = "도구 목록 조회", description = "예약 가능한 농기구들의 이름 목록을 반환합니다.")
    @GetMapping("/tools")
    public ResponseEntity<List<Tool>> getToolNames() {
//        List<String> toolNames = Arrays.stream(Tool.values())
//                .map(Enum::name)
//                .collect(Collectors.toList());
        List<Tool> toolNames = reservationService.getAllToolInventory()
                .stream().limit(5).toList();
        return ResponseEntity.ok(toolNames);
        //tool_inventory
    }

    @Operation(summary = "예약 생성", description = "새로운 농기구 예약을 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "예약 생성 성공"),
            @ApiResponse(responseCode = "400", description = "요청값 오류")
    })
    @PostMapping
    public ResponseEntity<Reservation> createReservation(
            @RequestBody ReservationRequestDto dto) {
        return ResponseEntity.ok(reservationService.createReservation(dto));
    }

    @Operation(summary = "전체 예약 조회", description = "모든 예약 목록을 반환합니다.")
    @GetMapping
    public ResponseEntity<List<Reservation>> getAllReservations() {
        return ResponseEntity.ok(reservationService.getAllReservations());
    }

    @Operation(summary = "예약 상세 조회", description = "특정 예약 ID로 예약 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "예약을 찾을 수 없음")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(
            @Parameter(description = "예약 ID", example = "1") @PathVariable Long id) {
        return reservationService.getReservationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "예약 수정", description = "예약 ID로 예약 내용을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "404", description = "예약을 찾을 수 없음")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(
            @Parameter(description = "예약 ID", example = "1") @PathVariable Long id,
            @RequestBody ReservationRequestDto dto) {
        return ResponseEntity.ok(reservationService.updateReservation(id, dto));
    }

    @Operation(summary = "예약 삭제", description = "예약 ID로 예약을 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(
            @Parameter(description = "예약 ID", example = "1") @PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }
}
