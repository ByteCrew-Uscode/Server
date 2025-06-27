package com.bytecrew.uscode.service;

import com.bytecrew.uscode.domain.Reservation;
import com.bytecrew.uscode.domain.Tool;
import com.bytecrew.uscode.domain.ToolInventory;
import com.bytecrew.uscode.dto.ReservationRequestDto;
import com.bytecrew.uscode.repository.ReservationRepository;
import com.bytecrew.uscode.repository.ToolInventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ToolInventoryRepository toolInventoryRepository;

    private ToolInventory getToolInventory(Tool tool) {
        return toolInventoryRepository.findByToolType(tool)
                .orElseThrow(() -> new IllegalStateException("해당 농기구 수량 정보가 없습니다."));
    }


    public Reservation createReservation(ReservationRequestDto dto) {
        ToolInventory inventory = getToolInventory(dto.getTool());

        if (inventory.getQuantity() <= 0) {
            throw new IllegalStateException("해당 농기구는 예약이 모두 소진되었습니다.");
        }
        inventory.setQuantity(inventory.getQuantity() - 1);

        Reservation reservation = Reservation.builder()
                .tool(dto.getTool())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .location(dto.getLocation())
                .reservationName(dto.getUserName())
                .build();
        return reservationRepository.save(reservation);
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Optional<Reservation> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }

    @Transactional
    public Reservation updateReservation(Long id, ReservationRequestDto dto) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 예약이 존재하지 않습니다."));

        Tool oldTool = reservation.getTool();
        Tool newTool = dto.getTool();

        ToolInventory oldInventory = getToolInventory(oldTool);
        oldInventory.setQuantity(oldInventory.getQuantity() + 1);

        ToolInventory newInventory = getToolInventory(newTool);
        if (newInventory.getQuantity() <= 0) {
            throw new IllegalStateException("변경하려는 농기구는 수량이 부족합니다.");
        }
        newInventory.setQuantity(newInventory.getQuantity() - 1);

        reservation.setTool(dto.getTool());
        reservation.setStartDate(dto.getStartDate());
        reservation.setEndDate(dto.getEndDate());
        reservation.setLocation(dto.getLocation());
        reservation.setReservationName(dto.getUserName());

        return reservationRepository.save(reservation);
    }

    @Transactional
    public void deleteReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 예약이 존재하지 않습니다."));

        ToolInventory inventory = getToolInventory(reservation.getTool());
        inventory.setQuantity(inventory.getQuantity() + 1);

        reservationRepository.deleteById(id);
    }
}