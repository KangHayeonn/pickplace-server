package com.server.pickplace.member.entity;

import javax.persistence.*;

import com.server.pickplace.common.common.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * description    :
 * packageName    : com.server.pickplace.member.entity
 * fileName       : Member
 * author         : tkfdk
 * date           : 2023-05-28
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-05-28        tkfdk       최초 생성
 */
@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "MEMBER_TB")
public class Member extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MEMBER_ID", nullable = false)
	private Long id;

	@Column(name = "MEMBER_EMAIL", nullable = false, length = 30)
	private String email;

	@Column(name = "MEMBER_PWD", nullable = false, length = 255)  // 일단 null 가능하게
	private String password;

	@Column(name = "MEMBER_PHONE", nullable = false, length = 13)
	private String number;

	@Column(name = "MEMBER_NICKNAME", nullable = false, length = 10)
	private String name;

	@Column(name = "MEMBER_ROLE", nullable = false)
	@Enumerated(EnumType.STRING)
	private MemberRole role;

}
