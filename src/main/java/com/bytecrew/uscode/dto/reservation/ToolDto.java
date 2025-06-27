package com.bytecrew.uscode.dto.reservation;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ToolDto {
    private Long id;
    private String name;
    private String description;
}

