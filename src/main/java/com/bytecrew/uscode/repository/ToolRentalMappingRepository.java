package com.bytecrew.uscode.repository;

import com.bytecrew.uscode.domain.Tool;
import com.bytecrew.uscode.domain.ToolRentalMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ToolRentalMappingRepository extends JpaRepository<ToolRentalMapping, Long> {
    List<ToolRentalMapping> findByToolAndQuantityGreaterThan(Tool tool, int qty);
}

