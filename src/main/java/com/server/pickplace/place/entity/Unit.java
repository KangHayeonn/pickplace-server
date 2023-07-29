package com.server.pickplace.place.entity;

import com.server.pickplace.common.common.BaseEntity;
import com.server.pickplace.reservation.entity.Reservation;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "UNIT_TB")
public class Unit extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UNIT_ID", nullable = false)
    private Long id;

    @Column(name = "UNIT_NAME", nullable = false, length = 20)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROOM_ID")
    private Room room;

    @OneToMany(mappedBy = "unit")
    @Builder.Default
    private List<Reservation> reservations = new ArrayList<>();

}
