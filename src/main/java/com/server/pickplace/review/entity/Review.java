package com.server.pickplace.review.entity;

import com.server.pickplace.common.common.BaseEntity;
import com.server.pickplace.reservation.entity.Reservation;

import javax.persistence.*;
import java.math.BigDecimal;

public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REVIEW_ID", nullable = false)
    private Long id;

    @Column(name = "REVIEW_CONTENT", nullable = true, length = 500)
    private String content;

    @Column(name = "REVIEW_RATING", nullable = false)
    private BigDecimal rating;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RESERVATION_ID")
    private Reservation reservation;
}
