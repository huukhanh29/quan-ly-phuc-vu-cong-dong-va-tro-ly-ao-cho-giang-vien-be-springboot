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

    @ManyToOne
    @JoinColumn(name = "maHoatDong")
    private HoatDong hoatDong;

    @Enumerated(EnumType.STRING)
    private TrangThaiDangKy trangThaiDangKy;
    public enum TrangThaiDangKy {
        ChoDuyet, ChoXacNhan, HoanThanh
    }

    public DangKyHoatDong(Long maDangKy, GiangVien giangVien, HoatDong hoatDong, TrangThaiDangKy trangThaiDangKy) {
        this.maDangKy = maDangKy;
        this.giangVien = giangVien;
        this.hoatDong = hoatDong;
        this.trangThaiDangKy = trangThaiDangKy;
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

