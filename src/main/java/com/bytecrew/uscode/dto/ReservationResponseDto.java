package com.bytecrew.uscode.dto;

import com.bytecrew.uscode.domain.Tool;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class ReservationResponseDto {
    private Long id;
    private String reservationName;
    private Tool tool;
    private LocalDate startDate;
    private LocalDate endDate;
    private String location;
    private String rentalLocation;
    private String image; // tool_inventory에서 가져온 이미지 경로
}
