package com.quanly.hoatdongcongdong.entity;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
public class RefreshToken {

    @ManyToOne
    @JoinColumn(name = "maTaiKhoan", referencedColumnName = "maTaiKhoan")
    private TaiKhoan taiKhoan;
    @Id
    @Column(nullable = false, unique = true, name = "refreshtoken")
    private String token;

    @Column(nullable = false, name = "ngayHetHan")
    private Instant expiryDate;


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