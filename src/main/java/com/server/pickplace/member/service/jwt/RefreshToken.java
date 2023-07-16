package com.server.pickplace.member.service.jwt;

import com.server.pickplace.config.ExpireTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Id;
import java.util.Collection;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "refresh", timeToLive = ExpireTime.REFRESH_TOKEN_EXPIRE_TIME_FOR_REDIS)
public class RefreshToken {
    @Id
//    private String id; ID로 받아서 하려했는데 토큰 페이로드 받아오는 과정에서 id 추출 도저히 안됨... => 어쩔수 없이 바로 accesstoken으로 접근
    private String accessToken;


    private String ip;

    private Collection<? extends GrantedAuthority> authorities;

    @Indexed
    private String refreshToken;
}
