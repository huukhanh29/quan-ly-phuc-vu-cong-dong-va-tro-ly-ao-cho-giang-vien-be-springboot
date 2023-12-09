package com.quanly.hoatdongcongdong.payload.response;

import com.quanly.hoatdongcongdong.entity.ChucDanh;
import com.quanly.hoatdongcongdong.entity.Khoa;
import com.quanly.hoatdongcongdong.entity.TaiKhoan;

public class GiangVienDTO {
    private Long maTaiKhoan;
    private Khoa khoa;
    private TaiKhoan taiKhoan;
    private ChucDanh chucDanh;
    private String trangThaiDangKy;

    public Long getMaTaiKhoan() {
        return maTaiKhoan;
    }

    public void setMaTaiKhoan(Long maTaiKhoan) {
        this.maTaiKhoan = maTaiKhoan;
    }

    public Khoa getKhoa() {
        return khoa;
    }

    public void setKhoa(Khoa khoa) {
        this.khoa = khoa;
    }

    public TaiKhoan getTaiKhoan() {
        return taiKhoan;
    }

    public void setTaiKhoan(TaiKhoan taiKhoan) {
        this.taiKhoan = taiKhoan;
    }

    public ChucDanh getChucDanh() {
        return chucDanh;
    }

    public void setChucDanh(ChucDanh chucDanh) {
        this.chucDanh = chucDanh;
    }

    public String getTrangThaiDangKy() {
        return trangThaiDangKy;
    }

    public void setTrangThaiDangKy(String trangThaiDangKy) {
        this.trangThaiDangKy = trangThaiDangKy;
    }
}
