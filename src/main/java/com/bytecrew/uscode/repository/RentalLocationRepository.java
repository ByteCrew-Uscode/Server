package com.bytecrew.uscode.repository;

import com.bytecrew.uscode.domain.RentalLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalLocationRepository extends JpaRepository<RentalLocation, Long> {
}
