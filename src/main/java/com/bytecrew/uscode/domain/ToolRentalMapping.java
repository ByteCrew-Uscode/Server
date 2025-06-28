package com.bytecrew.uscode.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class ToolRentalMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private ToolInventory toolInventory;  // 트랙터, 경운기 등

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rental_location_id")
    private RentalLocation rentalLocation;

    @Column()
    private Integer quantity;
}

/*
 이 구조는 ToolInventory와는 별개로, 조회용 매핑 테이블로만 사용됩니다.
 실제 예약 기능이나 재고 감소에는 관여하지 않고, 사용자에게 보여주는 정보만 제공합니다.
 */