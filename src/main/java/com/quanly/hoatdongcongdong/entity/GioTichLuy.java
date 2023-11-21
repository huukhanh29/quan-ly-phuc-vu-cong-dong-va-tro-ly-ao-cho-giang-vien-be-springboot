package com.quanly.hoatdongcongdong.entity;
import jakarta.persistence.*;
@Entity
public class GioTichLuy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maGioTichLuy;

    @ManyToOne
    @JoinColumn(name = "maGiangVien")
    private GiangVien giangVien;
    private int tongSoGio;
    private int gioMienGiam;
    private String nam;

    public GioTichLuy() {
    }

    public GioTichLuy(Long maGioTichLuy, GiangVien giangVien, int tongSoGio, int gioMienGiam, String nam) {
        this.maGioTichLuy = maGioTichLuy;
        this.giangVien = giangVien;
        this.tongSoGio = tongSoGio;
        this.gioMienGiam = gioMienGiam;
        this.nam = nam;
    }

    public int getGioMienGiam() {
        return gioMienGiam;
    }

    public void setGioMienGiam(int gioMienGiam) {
        this.gioMienGiam = gioMienGiam;
    }

    public Long getMaGioTichLuy() {
        return maGioTichLuy;
    }

    public void setMaGioTichLuy(Long maGioTichLuy) {
        this.maGioTichLuy = maGioTichLuy;
    }

    public GiangVien getGiangVien() {
        return giangVien;
    }

    public void setGiangVien(GiangVien giangVien) {
        this.giangVien = giangVien;
    }

    public int getTongSoGio() {
        return tongSoGio;
    }

    public void setTongSoGio(int tongSoGio) {
        this.tongSoGio = tongSoGio;
    }

    public String getNam() {
        return nam;
    }

    public void setNam(String namHoc) {
        this.nam = namHoc;
    }
}
