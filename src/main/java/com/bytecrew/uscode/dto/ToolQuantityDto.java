package com.bytecrew.uscode.dto;

import com.bytecrew.uscode.domain.Tool;

public record ToolQuantityDto(Tool tool, int quantity, String description, String image) {
}
