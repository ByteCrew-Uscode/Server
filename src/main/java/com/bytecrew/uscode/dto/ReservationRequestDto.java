package com.bytecrew.uscode.dto;

import com.bytecrew.uscode.domain.Tool;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationRequestDto {
    private Tool tool;
    private LocalDate startDate;
    private LocalDate endDate;
    private String location;
    private String userName;
    private String rentalLocation;
}