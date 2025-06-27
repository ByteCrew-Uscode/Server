package com.bytecrew.uscode.service;

import com.bytecrew.uscode.domain.Reservation;
import com.bytecrew.uscode.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    // 예약 생성
    public Reservation createReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    // 전체 예약 조회
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    // 단일 예약 조회
    public Optional<Reservation> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }

    // 예약 수정
    public Reservation updateReservation(Long id, Reservation updatedReservation) {
        return reservationRepository.findById(id)
                .map(reservation -> {
                    reservation.setTool(updatedReservation.getTool());
                    reservation.setStartDate(updatedReservation.getStartDate());
                    reservation.setEndDate(updatedReservation.getEndDate());
                    reservation.setLocation(updatedReservation.getLocation());
                    reservation.setReservationName(updatedReservation.getReservationName());
                    return reservationRepository.save(reservation);
                })
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found with id: " + id));
    }

    // 예약 삭제
    public void deleteReservation(Long id) {
        reservationRepository.deleteById(id);
    }
}
