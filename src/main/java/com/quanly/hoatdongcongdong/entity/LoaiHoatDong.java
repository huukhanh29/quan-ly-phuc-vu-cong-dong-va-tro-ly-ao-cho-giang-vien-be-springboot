package com.quanly.hoatdongcongdong.entity;
import jakarta.persistence.*;
@Entity
@Table(name = "LoaiHoatDong")
public class LoaiHoatDong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maLoaiHoatDong;
    @Column(columnDefinition = "TEXT")
    private String tenLoaiHoatDong;
    @Column(columnDefinition = "TEXT")
    private String moTa;

    public LoaiHoatDong() {
    }

    public LoaiHoatDong(String tenLoaiHoatDong, String moTa) {
        this.tenLoaiHoatDong = tenLoaiHoatDong;
        this.moTa = moTa;
    }

    public Long getMaLoaiHoatDong() {
        return maLoaiHoatDong;
    }

    public void setMaLoaiHoatDong(Long maLoaiHoatDong) {
        this.maLoaiHoatDong = maLoaiHoatDong;
    }

    public String getTenLoaiHoatDong() {
        return tenLoaiHoatDong;
    }

    public void setTenLoaiHoatDong(String tenLoaiHoatDong) {
        this.tenLoaiHoatDong = tenLoaiHoatDong;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }
}
