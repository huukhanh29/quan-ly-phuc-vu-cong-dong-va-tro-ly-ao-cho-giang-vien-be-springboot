package com.quanly.hoatdongcongdong.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "GiangVien")
public class GiangVien {

    @Id
    private Long maTaiKhoan;

    @ManyToOne
    @JoinColumn(name = "maChucDanh")
    private ChucDanh chucDanh;

    @OneToOne
    @JoinColumn(name = "maTaiKhoan", referencedColumnName = "maTaiKhoan", insertable = false, updatable = false)
    @MapsId
    private TaiKhoan taiKhoan;
    @ManyToOne
    @JoinColumn(name = "maKhoa")
    private Khoa khoa;
    public GiangVien() {
    }

    public GiangVien(Long maTaiKhoan, ChucDanh chucDanh, TaiKhoan taiKhoan, Khoa khoa) {
        this.maTaiKhoan = maTaiKhoan;
        this.chucDanh = chucDanh;
        this.taiKhoan = taiKhoan;
        this.khoa = khoa;
    }

    public Khoa getKhoa() {
        return khoa;
    }

    public void setKhoa(Khoa khoa) {
        this.khoa = khoa;
    }

    public Long getMaTaiKhoan() {
        return maTaiKhoan;
    }

    public void setMaTaiKhoan(Long maTaiKhoan) {
        this.maTaiKhoan = maTaiKhoan;
    }

    public ChucDanh getChucDanh() {
        return chucDanh;
    }

    public void setChucDanh(ChucDanh chucDanh) {
        this.chucDanh = chucDanh;
    }

    public TaiKhoan getTaiKhoan() {
        return taiKhoan;
    }

    public void setTaiKhoan(TaiKhoan taiKhoan) {
        this.taiKhoan = taiKhoan;
    }

}
