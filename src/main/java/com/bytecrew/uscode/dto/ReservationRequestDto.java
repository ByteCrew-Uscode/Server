package com.bytecrew.uscode.dto;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class ReservationRequestDto {
    private Long toolId;
    private String userName;
}

