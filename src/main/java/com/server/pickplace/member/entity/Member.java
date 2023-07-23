package com.server.pickplace.member.entity;

import javax.persistence.*;

import com.server.pickplace.member.dto.MemberSignupRequestDto;
import com.server.pickplace.common.common.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;

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
 * 2023-06-23		 sohyun		 비밀번호 암호화 추가 / dto 생성자 추가
 */
@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "MEMBER_TB")
public class Member extends BaseEntity implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MEMBER_ID", nullable = false)
	private Long id;

	@Column(name = "MEMBER_EMAIL", nullable = false, length = 30, unique = true)
	private String email;

	@Column(name = "MEMBER_TYPE", nullable = false, length = 10)
	private String type; //일반 / 카카오

	@Column(name = "MEMBER_PWD", nullable = false, length = 255)  // 일단 null 가능하게
	private String password;

	@Column(name = "MEMBER_PHONE", nullable = false, length = 13)
	private String number;

	@Column(name = "MEMBER_NICKNAME", nullable = false, length = 10)
	private String name;

	@Column(name = "MEMBER_ROLE", nullable = false)
	@Enumerated(EnumType.STRING)
	private MemberRole role;


	public Member(MemberSignupRequestDto request) {
		email = request.getEmail();
		password = request.getPassword();
		number = request.getPhone();
		name = request.getNickname();
		role = role.USER; // 회원가입하는 사용자 권한 기본 USER (임시)
	}

	public Member(String kakaoEmail, String nickname, String profile, String encodedPassword) {
		super();
	}


	//비밀번호 암호화
	public void encryptPassword(PasswordEncoder passwordEncoder) {
		password = passwordEncoder.encode(password);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return null;
	}

	// 이메일과 비밀번호로 인증 진행 email , password
	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public boolean isAccountNonExpired() {
		return false;
	}

	@Override
	public boolean isAccountNonLocked() {
		return false;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return false;
	}

	@Override
	public boolean isEnabled() {
		return false;
	}
}
