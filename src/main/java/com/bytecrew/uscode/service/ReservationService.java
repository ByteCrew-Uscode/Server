package com.bytecrew.uscode.service;

import com.bytecrew.uscode.domain.Reservation;
import com.bytecrew.uscode.domain.Tool;
import com.bytecrew.uscode.domain.ToolInventory;
import com.bytecrew.uscode.dto.ReservationRequestDto;
import com.bytecrew.uscode.dto.ReservationResponseDto;
import com.bytecrew.uscode.dto.ToolInventoryAdd;
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

    public List<ToolInventory> getAllToolInventory() {
        return toolInventoryRepository.findAll();
    }

    public List<Tool> getAllTools(){
        return toolInventoryRepository.findAllToolTypes();
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

    public List<ReservationResponseDto> getAllReservations() {
        List<Reservation> reservations = reservationRepository.findAll();

        return reservations.stream().map(reservation -> {
            ToolInventory inventory = toolInventoryRepository.findByToolType(reservation.getTool())
                    .orElseThrow(() -> new IllegalArgumentException("해당 도구에 대한 이미지 없음"));

            return ReservationResponseDto.builder()
                    .id(reservation.getId())
                    .reservationName(reservation.getReservationName())
                    .tool(reservation.getTool())
                    .startDate(reservation.getStartDate())
                    .endDate(reservation.getEndDate())
                    .location(reservation.getLocation())
                    .rentalLocation(reservation.getRentalLocation())
                    .image(inventory.getImage())
                    .build();
        }).toList();
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

    @Transactional(readOnly = true)
    public List<ToolInventoryAdd> getAllReservationDetails() {
        List<Reservation> reservations = reservationRepository.findAll();

        return reservations.stream().map(reservation -> {
            Tool tool = reservation.getTool();

            ToolInventory inventory = toolInventoryRepository.findByToolType(tool)
                    .orElseThrow(() -> new IllegalStateException("해당 도구 정보가 존재하지 않습니다."));

            // location을 reservation에 문자열로 저장 중이라면 그대로 사용
            return new ToolInventoryAdd(
                    tool,
                    reservation.getStartDate(),
                    reservation.getEndDate(),
                    reservation.getRentalLocation(),
                    reservation.getReservationName(),
                    inventory.getDescription(),
                    inventory.getImage()
            );
        }).toList();
    }

}