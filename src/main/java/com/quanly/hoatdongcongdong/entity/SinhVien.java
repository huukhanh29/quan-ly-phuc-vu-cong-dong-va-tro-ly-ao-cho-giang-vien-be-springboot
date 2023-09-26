package com.quanly.hoatdongcongdong.entity;
import jakarta.persistence.*;

import java.time.Year;

@Entity
@Table(name = "SinhVien")
public class SinhVien {

    @Id
    private Long maTaiKhoan;

    private Year namNhapHoc;

    @OneToOne
    @JoinColumn(name = "maTaiKhoan", referencedColumnName = "maTaiKhoan", insertable = false, updatable = false)
    @MapsId
    private TaiKhoan taiKhoan;


    public SinhVien(Long maTaiKhoan, Year namNhapHoc,
                  TaiKhoan taiKhoan) {
        this.maTaiKhoan = maTaiKhoan;
        this.namNhapHoc = namNhapHoc;
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

    public TaiKhoan getTaiKhoan() {
        return taiKhoan;
    }

    public void setTaiKhoan(TaiKhoan taiKhoan) {
        this.taiKhoan = taiKhoan;
    }

}

