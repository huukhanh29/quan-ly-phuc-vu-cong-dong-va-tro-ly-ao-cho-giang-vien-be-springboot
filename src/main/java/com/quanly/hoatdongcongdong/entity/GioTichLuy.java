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
    private String nam;

    public GioTichLuy() {
    }

    public GioTichLuy(Long maGioTichLuy, GiangVien giangVien, int tongSoGio, String nam) {
        this.maGioTichLuy = maGioTichLuy;
        this.giangVien = giangVien;
        this.tongSoGio = tongSoGio;
        this.nam = nam;
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
