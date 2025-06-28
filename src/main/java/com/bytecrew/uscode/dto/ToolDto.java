package com.bytecrew.uscode.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ToolDto {
    private Long id;
    private String name;
    private Integer quantity;
    private String description;
}

