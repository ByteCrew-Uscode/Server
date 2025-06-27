package com.bytecrew.uscode.dto;


public record ToolRentalLocationDto(
        String location,
        String address,
        int availableCount,
        String phone
) {}

