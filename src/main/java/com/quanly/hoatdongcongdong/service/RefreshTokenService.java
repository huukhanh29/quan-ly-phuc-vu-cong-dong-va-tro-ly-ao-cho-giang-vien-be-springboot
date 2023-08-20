package com.quanly.hoatdongcongdong.service;

import com.quanly.hoatdongcongdong.entity.RefreshToken;
import com.quanly.hoatdongcongdong.exception.TokenRefreshException;
import com.quanly.hoatdongcongdong.repository.TaiKhoanRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import com.quanly.hoatdongcongdong.repository.RefreshTokenRepository;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
    @Value(" 604800000")
    private Long refreshTokenDurationMs;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }
    public RefreshToken createRefreshToken(Long maTK) {

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setTaiKhoan(taiKhoanRepository.findByMaTaiKhoan(maTK).get());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken = refreshTokenRepository.save(refreshToken);

        return refreshToken;
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(),
                    "Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    @Transactional
    public int deleteByMaTaiKhoan(Long maTK) {
        return refreshTokenRepository.deleteByTaiKhoan(taiKhoanRepository.findById(maTK).get());
    }

    //tự động xóa các refreshtoken sau 1 ngày.
    @Scheduled(fixedRateString = "86400000")
    public void removeExpiredRefreshTokens() {
        List<RefreshToken> expiredTokens = refreshTokenRepository.findAllByExpiryDateBefore(Instant.now());
        refreshTokenRepository.deleteAll(expiredTokens);
    }
}