package com.bytecrew.uscode.dto;

import com.bytecrew.uscode.domain.Tool;
import lombok.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationRequestDto {
    private Tool tool;
    private Date startDate;
    private Date endDate;
    private String location;
    private String userName;
}