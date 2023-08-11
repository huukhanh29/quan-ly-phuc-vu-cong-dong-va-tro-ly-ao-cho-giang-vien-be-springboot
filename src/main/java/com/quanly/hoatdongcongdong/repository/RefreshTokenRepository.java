package com.quanly.hoatdongcongdong.repository;
import com.quanly.hoatdongcongdong.entity.RefreshToken;
import com.quanly.hoatdongcongdong.entity.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    @Modifying
    int deleteByTaiKhoan(TaiKhoan taiKhoan);
    List<RefreshToken> findAllByExpiryDateBefore(Instant expiryDate);
}