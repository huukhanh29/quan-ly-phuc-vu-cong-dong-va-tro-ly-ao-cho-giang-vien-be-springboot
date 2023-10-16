package com.quanly.hoatdongcongdong.entity;
import jakarta.persistence.*;
@Entity
@Table(name = "Khoa")
public class Khoa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maKhoa;

    private String tenKhoa;

    @ManyToOne
    @JoinColumn(name = "maTruong")
    private Truong truong;

    public Khoa() {
    }

    public Khoa(Long maKhoa, String tenKhoa, Truong truong) {
        this.maKhoa = maKhoa;
        this.tenKhoa = tenKhoa;
        this.truong = truong;
    }

    public Long getMaKhoa() {
        return maKhoa;
    }

    public void setMaKhoa(Long maKhoa) {
        this.maKhoa = maKhoa;
    }

    public String getTenKhoa() {
        return tenKhoa;
    }

    public void setTenKhoa(String tenKhoa) {
        this.tenKhoa = tenKhoa;
    }

    public Truong getTruong() {
        return truong;
    }

    public void setTruong(Truong truong) {
        this.truong = truong;
    }
}
