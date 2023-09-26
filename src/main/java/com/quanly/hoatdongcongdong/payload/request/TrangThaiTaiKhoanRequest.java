package com.quanly.hoatdongcongdong.payload.request;


import com.quanly.hoatdongcongdong.entity.TaiKhoan;

public class TrangThaiTaiKhoanRequest {
    private String tenDangNhap;
    private TaiKhoan.TrangThai trangThai;

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public TaiKhoan.TrangThai getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TaiKhoan.TrangThai trangThai) {
        this.trangThai = trangThai;
    }
}
