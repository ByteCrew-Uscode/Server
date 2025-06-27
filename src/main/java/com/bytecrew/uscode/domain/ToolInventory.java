package com.bytecrew.uscode.domain;

import jakarta.persistence.*;
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

    @Column(nullable = true) // 수량은 수동으로 관리하므로 nullable 허용
    private Integer quantity;
}

