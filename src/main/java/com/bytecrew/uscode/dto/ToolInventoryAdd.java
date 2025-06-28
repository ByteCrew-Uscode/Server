package com.bytecrew.uscode.dto;

import com.bytecrew.uscode.domain.Tool;

import java.time.LocalDate;

public record ToolInventoryAdd(
        Tool tool,
        LocalDate startDate,
        LocalDate endDate,
        String location, //사업소
        String userName, //신청자
        String description,
        String image
) {
}
