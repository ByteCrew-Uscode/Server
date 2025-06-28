package com.bytecrew.uscode.service;

import com.bytecrew.uscode.domain.RentalLocation;
import com.bytecrew.uscode.domain.Tool;
import com.bytecrew.uscode.domain.ToolInventory;
import com.bytecrew.uscode.dto.ToolQuantityDto;
import com.bytecrew.uscode.dto.ToolRentalLocationDto;
import com.bytecrew.uscode.repository.RentalLocationRepository;
import com.bytecrew.uscode.repository.ToolInventoryRepository;
import com.bytecrew.uscode.repository.ToolRentalMappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ToolLocationService {

    private final ToolRentalMappingRepository mappingRepository;
    private final RentalLocationRepository rentalLocationRepository;
    private final ToolInventoryRepository toolInventoryRepository;

    public List<ToolRentalLocationDto> getLocationsByTool(Tool tool) {
        ToolInventory toolInventory = toolInventoryRepository.findByToolType(tool).get();
        return mappingRepository.findByToolInventoryAndToolInventory_QuantityGreaterThan(toolInventory, 0)
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
        return mappingRepository.findByRentalLocationIdAndToolInventory_QuantityGreaterThan(rentalLocationId, 0)
                .stream()
                .map(mapping -> new ToolQuantityDto(
                        mapping.getToolInventory().getToolType(),
                        mapping.getQuantity(),
                        mapping.getToolInventory().getDescription()
                ))
                .collect(Collectors.toList());
    }

    public List<RentalLocation> getRentalLocations() {
        return rentalLocationRepository.findAll();
    }
}


