package com.quanly.hoatdongcongdong.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "DangKyHoatDong")
public class DangKyHoatDong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maDangKy;

    @ManyToOne
    @JoinColumn(name = "maGiangVien")
    private GiangVien giangVien;
    @Column(columnDefinition = "TEXT")
    private String lyDoHuy;
    @ManyToOne
    @JoinColumn(name = "maHoatDong")
    private HoatDong hoatDong;

    @Enumerated(EnumType.STRING)
    private TrangThaiDangKy trangThaiDangKy;

    public enum TrangThaiDangKy {
        Chua_Duyet, Da_Duyet, Da_Huy
    }

    public DangKyHoatDong(Long maDangKy, GiangVien giangVien, String lyDoHuy, HoatDong hoatDong, TrangThaiDangKy trangThaiDangKy) {
        this.maDangKy = maDangKy;
        this.giangVien = giangVien;
        this.lyDoHuy = lyDoHuy;
        this.hoatDong = hoatDong;
        this.trangThaiDangKy = trangThaiDangKy;
    }

    public String getLyDoHuy() {
        return lyDoHuy;
    }

    public void setLyDoHuy(String lyDoHuy) {
        this.lyDoHuy = lyDoHuy;
    }

    public Long getMaDangKy() {
        return maDangKy;
    }

    public void setMaDangKy(Long maDangKy) {
        this.maDangKy = maDangKy;
    }

    public GiangVien getGiangVien() {
        return giangVien;
    }

    public void setGiangVien(GiangVien giangVien) {
        this.giangVien = giangVien;
    }

    public HoatDong getHoatDong() {
        return hoatDong;
    }

    public void setHoatDong(HoatDong hoatDong) {
        this.hoatDong = hoatDong;
    }

    public TrangThaiDangKy getTrangThaiDangKy() {
        return trangThaiDangKy;
    }

    public void setTrangThaiDangKy(TrangThaiDangKy trangThaiDangKy) {
        this.trangThaiDangKy = trangThaiDangKy;
    }

    public DangKyHoatDong() {
    }
}

