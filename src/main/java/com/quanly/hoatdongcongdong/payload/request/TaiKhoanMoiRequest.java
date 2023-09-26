package com.quanly.hoatdongcongdong.payload.request;

import com.quanly.hoatdongcongdong.entity.ChucDanh;
import com.quanly.hoatdongcongdong.entity.TaiKhoan;

import java.time.Year;
import java.util.Date;

public class TaiKhoanMoiRequest {
    private String tenDangNhap;
    private String matKhau;
    private String email;
    private String tenDayDu;
    private TaiKhoan.GioiTinh gioiTinh;
    private TaiKhoan.Quyen quyen;
    private String soDienThoai;
    private Date ngaySinh;
    private String diaChi;
    private Year namNhapHoc; // Dùng cho sinh viên
    private Long maChucDanh; // Dùng cho giảng viên

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public TaiKhoan.GioiTinh getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(TaiKhoan.GioiTinh gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTenDayDu() {
        return tenDayDu;
    }

    public void setTenDayDu(String tenDayDu) {
        this.tenDayDu = tenDayDu;
    }

    public TaiKhoan.Quyen getQuyen() {
        return quyen;
    }

    public void setQuyen(TaiKhoan.Quyen quyen) {
        this.quyen = quyen;
    }

    public Year getNamNhapHoc() {
        return namNhapHoc;
    }

    public void setNamNhapHoc(Year namNhapHoc) {
        this.namNhapHoc = namNhapHoc;
    }

    public Long getMaChucDanh() {
        return maChucDanh;
    }

    public void setMaChucDanh(Long maChucDanh) {
        this.maChucDanh = maChucDanh;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public Date getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(Date ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }
}
