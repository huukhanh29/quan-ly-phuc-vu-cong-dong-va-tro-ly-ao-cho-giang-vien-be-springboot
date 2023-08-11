package com.quanly.hoatdongcongdong.entity;
import jakarta.persistence.*;

import java.time.Year;

@Entity
@Table(name = "SinhVien")
public class SinhVien {

    @Id
    private Long maTaiKhoan;

    private Year namNhapHoc;

    @Column(nullable = false)
    private String chuyenNganh;

    @OneToOne
    @JoinColumn(name = "maTaiKhoan", referencedColumnName = "maTaiKhoan", insertable = false, updatable = false)
    @MapsId
    private TaiKhoan taiKhoan;


    public SinhVien(Long maTaiKhoan, Year namNhapHoc,
                    String chuyenNganh, TaiKhoan taiKhoan) {
        this.maTaiKhoan = maTaiKhoan;
        this.namNhapHoc = namNhapHoc;
        this.chuyenNganh = chuyenNganh;
        this.taiKhoan = taiKhoan;
    }

    public SinhVien() {
    }
    public Long getMaTaiKhoan() {
        return maTaiKhoan;
    }

    public void setMaTaiKhoan(Long maTaiKhoan) {
        this.maTaiKhoan = maTaiKhoan;
    }


    public Year getNamNhapHoc() {
        return namNhapHoc;
    }

    public void setNamNhapHoc(Year namNhapHoc) {
        this.namNhapHoc = namNhapHoc;
    }

    public String getChuyenNganh() {
        return chuyenNganh;
    }

    public void setChuyenNganh(String chuyenNganh) {
        this.chuyenNganh = chuyenNganh;
    }

    public TaiKhoan getTaiKhoan() {
        return taiKhoan;
    }

    public void setTaiKhoan(TaiKhoan taiKhoan) {
        this.taiKhoan = taiKhoan;
    }

}

