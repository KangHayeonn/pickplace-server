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
@Table(name = "TAG_TB")
public class Tag extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TAG_ID", nullable = false)
    private Long id;

    @Column(name = "TAG_NAME", nullable = false)
    @Enumerated(EnumType.STRING)
    private TagStatus tagStatus;

}
