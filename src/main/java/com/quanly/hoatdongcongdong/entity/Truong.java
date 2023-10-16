package com.quanly.hoatdongcongdong.entity;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "Truong")
public class Truong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maTruong;

    private String tenTruong;

    public Truong() {
    }

    public Truong(Long maTruong, String tenTruong) {
        this.maTruong = maTruong;
        this.tenTruong = tenTruong;

    }

    public Long getMaTruong() {
        return maTruong;
    }

    public void setMaTruong(Long maTruong) {
        this.maTruong = maTruong;
    }

    public String getTenTruong() {
        return tenTruong;
    }

    public void setTenTruong(String tenTruong) {
        this.tenTruong = tenTruong;
    }

}
