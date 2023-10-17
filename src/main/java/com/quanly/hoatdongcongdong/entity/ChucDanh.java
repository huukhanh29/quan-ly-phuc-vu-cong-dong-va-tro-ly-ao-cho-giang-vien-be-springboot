package com.quanly.hoatdongcongdong.entity;
import jakarta.persistence.*;
@Entity
@Table(name = "ChucDanh")
public class ChucDanh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maChucDanh;

    private String tenChucDanh;


    private int gioBatBuoc;

    public ChucDanh() {

    }

    public ChucDanh(String tenChucDanh, int gioBatBuoc) {
        this.tenChucDanh = tenChucDanh;
        this.gioBatBuoc = gioBatBuoc;
    }

    public Long getMaChucDanh() {
        return maChucDanh;
    }

    public void setMaChucDanh(Long maChucDanh) {
        this.maChucDanh = maChucDanh;
    }

    public String getTenChucDanh() {
        return tenChucDanh;
    }

    public void setTenChucDanh(String tenChucDanh) {
        this.tenChucDanh = tenChucDanh;
    }

    public int getGioBatBuoc() {
        return gioBatBuoc;
    }

    public void setGioBatBuoc(int gioBatBuoc) {
        this.gioBatBuoc = gioBatBuoc;
    }

}
