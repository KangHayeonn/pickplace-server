package com.server.pickplace.place.entity;


import com.server.pickplace.common.common.BaseEntity;
import com.server.pickplace.reservation.entity.ReservationStatus;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "CATEGORY_TB")
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CATEGORY_ID", nullable = false)
    private Long id;

    @Column(name = "CATEGORY_NAME", nullable = false)
    @Enumerated(EnumType.STRING)
    private CategoryStatus status;


}
