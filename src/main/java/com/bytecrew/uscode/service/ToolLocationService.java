package com.bytecrew.uscode.service;

import com.bytecrew.uscode.domain.RentalLocation;
import com.bytecrew.uscode.domain.Tool;
import com.bytecrew.uscode.dto.ToolQuantityDto;
import com.bytecrew.uscode.dto.ToolRentalLocationDto;
import com.bytecrew.uscode.repository.ToolRentalMappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ToolLocationService {

    private final ToolRentalMappingRepository mappingRepository;

    public List<ToolRentalLocationDto> getLocationsByTool(Tool tool) {
        return mappingRepository.findByToolAndQuantityGreaterThan(tool, 0)
                .stream()
                .map(mapping -> {
                    RentalLocation loc = mapping.getRentalLocation();
                    return new ToolRentalLocationDto(
                            loc.getLocationName(),
                            loc.getAddress(),
                            mapping.getQuantity(),
                            loc.getPhone()
                    );
                })
                .collect(Collectors.toList());
    }
    public List<ToolQuantityDto> getToolsByRentalLocation(Long rentalLocationId) {
        return mappingRepository.findByRentalLocationIdAndQuantityGreaterThan(rentalLocationId, 0)
                .stream()
                .map(mapping -> new ToolQuantityDto(
                        mapping.getTool(),
                        mapping.getQuantity()
                ))
                .collect(Collectors.toList());
    }
}

