package com.bytecrew.uscode.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //도구 이름 Enum 형식
    @Enumerated(EnumType.STRING)
    private Tool tool;

    //대여 시작 일자
    private Date startDate;

    //반납일자
    private Date endDate;

    //예약 위치
    private String location;


    //예약자 성명
    private String reservationName;

    @Version
    private Long version;

    @Builder
    public Reservation(Tool tool, Date startDate, Date endDate, String location, String reservationName) {
        this.tool = tool;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
        this.reservationName = reservationName;
    }
}
