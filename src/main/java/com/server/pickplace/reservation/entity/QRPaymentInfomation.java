package com.server.pickplace.reservation.entity;

import com.server.pickplace.common.common.BaseEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "QR_PAYMENT_TB")
public class QRPaymentInfomation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "QR_PAYMENT_ID", nullable = false)
    private Long id;

    @Column(name = "QR_PAYMENT_CODE", nullable = false, length = 36)
    private String qrPaymentCode;

    @Column(name = "QR_PAYMENT_MEMBER_ID", nullable = false)
    private Long memberId;

    @Column(name = "QR_PAYMENT_PRICE", nullable = false)
    private Integer price;

    @Column(name = "QR_PAYMENT_STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private QRStatus status;





}
