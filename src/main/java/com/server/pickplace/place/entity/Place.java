package com.server.pickplace.place.entity;

import com.server.pickplace.common.common.BaseEntity;
import com.server.pickplace.member.entity.Member;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.geo.Point;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "PLACE_TB")
@DynamicInsert
@ToString(exclude = "member")
public class Place extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PLACE_ID", nullable = false)
    private Long id;

    @Column(name = "PLACE_ADDRESS", nullable = false, length = 255)
    private String address;  // 나중에 값 타입

    @Column(name = "PLACE_PHONE", nullable = true, length = 13)
    private String number;

    @Column(name = "PLACE_NAME", nullable = false, length = 20)
    private String name;

    @Column(name = "PLACE_TOTAL_RATING", nullable = false)
    @ColumnDefault(value = "0")
    private Float rating;

    @Column(name = "PLACE_REVIEW_CNT", nullable = false)
    @ColumnDefault(value = "0")
    private Integer reviewCount;

    @Column(name = "PLACE_POINT", nullable = false)
    private Point point;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @OneToMany(mappedBy = "place")
    private List<CategoryPlace> categories = new ArrayList<>();

    @OneToMany(mappedBy = "place")
    private List<TagPlace> tags = new ArrayList<>();

    @OneToMany(mappedBy = "place")
    private List<Room> rooms = new ArrayList<>();




}
