package com.quanly.hoatdongcongdong.repository;
import com.quanly.hoatdongcongdong.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByRefreshtoken(String token);
    @Modifying
    void deleteByRefreshtoken(String refreshToken);
    List<RefreshToken> findAllByExpiryDateBefore(Instant expiryDate);
}