package com.server.pickplace.place.entity;

import com.server.pickplace.common.common.BaseEntity;
import com.server.pickplace.member.entity.Member;
import lombok.*;
import org.springframework.data.geo.Point;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "PLACE_TB")
public class Place extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PLACE_ID", nullable = false)
    private Long id;

    @Column(name = "PLACE_ADDRESS", nullable = false, length = 255)
    private String address;  // 나중에 값 타입

    @Column(name = "PLACE_PHONE", nullable = true, length = 13)
    private String number;

    @Column(name = "PLACE_TOTAL_RATING", nullable = true)
    private Float rating;

    @Column(name = "PLACE_REVIEW_CNT", nullable = false)
    private Integer reviewCount;

    @Column(name = "PLACE_POINT", nullable = false)
    private Point point;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;
}