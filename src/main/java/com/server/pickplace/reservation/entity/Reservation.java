package com.server.pickplace.reservation.entity;

import com.server.pickplace.common.common.BaseEntity;
import com.server.pickplace.member.entity.Member;
import com.server.pickplace.place.entity.Room;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RESERVATION_TB")
public class Reservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RESERVATION_ID", nullable = false)
    private Long id;

    @Column(name = "RESERVATION_STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    // https://m.blog.naver.com/nieah914/221810697040 -> MYSQL
    // https://hianna.tistory.com/607  -> JAVA

    @Column(name = "START_DATE", nullable = false)
    private LocalDate startDate;

    @Column(name = "START_TIME", nullable = false)
    private LocalTime startTime;

    @Column(name = "END_DATE", nullable = false)
    private LocalDate endDate;

    @Column(name = "END_TIME", nullable = false)
    private LocalTime endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROOM_ID")
    private Room room;
}
