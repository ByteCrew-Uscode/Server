package com.bytecrew.uscode.repository;

import com.bytecrew.uscode.domain.Tool;
import com.bytecrew.uscode.domain.ToolInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ToolInventoryRepository extends JpaRepository<ToolInventory, Long> {
    Optional<ToolInventory> findByToolType(Tool toolType);

    @Query("SELECT t.toolType FROM ToolInventory t")
    List<Tool> findAllToolTypes();
}

