package com.bytecrew.uscode.repository;

import com.bytecrew.uscode.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

}
