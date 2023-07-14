package com.server.pickplace.member.repository;

import com.server.pickplace.member.service.jwt.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRedisRepository extends CrudRepository<RefreshToken, Long> {

    RefreshToken findByRefreshToken(String refreshToken);
}
