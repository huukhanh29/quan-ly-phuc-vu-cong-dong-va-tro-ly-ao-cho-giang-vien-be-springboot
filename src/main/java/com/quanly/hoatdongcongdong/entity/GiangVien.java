package com.quanly.hoatdongcongdong.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "GiangVien")
public class GiangVien {

    @Id
    private Long maTaiKhoan;

    @Column(nullable = false)
    private String chucDanh;

    @OneToOne
    @JoinColumn(name = "maTaiKhoan", referencedColumnName = "maTaiKhoan", insertable = false, updatable = false)
    @MapsId
    private TaiKhoan taiKhoan;

    public GiangVien() {
    }

    public GiangVien(Long maTaiKhoan, String chucDanh, TaiKhoan taiKhoan) {
        this.maTaiKhoan = maTaiKhoan;
        this.chucDanh = chucDanh;
        this.taiKhoan = taiKhoan;
    }

    public Long getMaTaiKhoan() {
        return maTaiKhoan;
    }

    public void setMaTaiKhoan(Long maTaiKhoan) {
        this.maTaiKhoan = maTaiKhoan;
    }

    public String getChucDanh() {
        return chucDanh;
    }

    public void setChucDanh(String chucDanh) {
        this.chucDanh = chucDanh;
    }

    public TaiKhoan getTaiKhoan() {
        return taiKhoan;
    }

    public void setTaiKhoan(TaiKhoan taiKhoan) {
        this.taiKhoan = taiKhoan;
    }
}
