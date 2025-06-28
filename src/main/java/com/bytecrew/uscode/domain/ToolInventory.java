package com.bytecrew.uscode.domain;

import com.bytecrew.uscode.dto.ReservationRequestDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ToolInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private Tool toolType;

    @Column(nullable = false)
    @Min(0)
    private Integer quantity;

    @Column(nullable = true)
    private String description;

    public Tool getToolType(ReservationRequestDto reservationRequestDto){
        return this.toolType;
    }
}

