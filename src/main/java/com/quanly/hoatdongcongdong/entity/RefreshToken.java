package com.quanly.hoatdongcongdong.entity;
import jakarta.persistence.*;

import java.time.Instant;

@Entity(name = "refreshtoken")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long maToken;

    @ManyToOne
    @JoinColumn(name = "maTaiKhoan", referencedColumnName = "maTaiKhoan")
    private TaiKhoan taiKhoan;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false, name = "ngayHetHan")
    private Instant expiryDate;

    public long getMaToken() {
        return maToken;
    }

    public void setMaToken(long maToken) {
        this.maToken = maToken;
    }

    public TaiKhoan getTaiKhoan() {
        return taiKhoan;
    }

    public void setTaiKhoan(TaiKhoan taiKhoan) {
        this.taiKhoan = taiKhoan;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Instant getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Instant expiryDate) {
        this.expiryDate = expiryDate;
    }

}