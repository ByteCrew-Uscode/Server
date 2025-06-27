package com.bytecrew.uscode.controller;

import com.bytecrew.uscode.domain.Tool;
import com.bytecrew.uscode.dto.ToolRentalLocationDto;
import com.bytecrew.uscode.service.ToolLocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tools")
public class ToolController {

    private final ToolLocationService toolLocationService;

    @GetMapping("/{tool}/locations")
    public ResponseEntity<List<ToolRentalLocationDto>> getToolLocations(@PathVariable Tool tool) {
        return ResponseEntity.ok(toolLocationService.getLocationsByTool(tool));
    }
}

