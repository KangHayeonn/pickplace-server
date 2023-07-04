package com.server.pickplace.place.entity;

import com.server.pickplace.common.common.BaseEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ROOM_TB")
@ToString(exclude = "place")
public class Room extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ROOM_ID", nullable = false)
    private Long id;

    @Column(name = "ROOM_PRICE", nullable = false)
    private Integer price;

    @Column(name = "ROOM_NAME", nullable = false, length = 20)
    private String name;

    @Column(name = "ROOM_AMOUNT", nullable = false)
    private Integer amount;

    @Column(name = "PEOPLE_NUM", nullable = false)
    private Integer peopleNum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PLACE_ID")
    private Place place;

}
