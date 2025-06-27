package com.bytecrew.uscode.repository;

import com.bytecrew.uscode.domain.Tool;
import com.bytecrew.uscode.domain.ToolInventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ToolInventoryRepository extends JpaRepository<ToolInventory, Long> {
    Optional<ToolInventory> findByToolType(Tool toolType);
}

