package com.bytecrew.uscode.dto.reservation;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class ReservationRequestDto {
    private Long toolId;
    private String userName;
}

