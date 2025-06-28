package com.bytecrew.uscode.controller;

import com.bytecrew.uscode.domain.RentalLocation;
import com.bytecrew.uscode.dto.ToolQuantityDto;
import com.bytecrew.uscode.service.ToolLocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/locations")
public class RentalLocationController {

    @Autowired
    private final ToolLocationService toolLocationService;

    @GetMapping("/{locationId}/tools")
    public ResponseEntity<List<ToolQuantityDto>> getToolsByLocation(@PathVariable Long locationId) {
        return ResponseEntity.ok(toolLocationService.getToolsByRentalLocation(locationId));
    }

    @GetMapping("/")
    public ResponseEntity<List<RentalLocation>> getAllTools() {
        return ResponseEntity.ok(toolLocationService.getRentalLocations());
    }
}

