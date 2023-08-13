package com.quanly.hoatdongcongdong.payload.response;

import com.quanly.hoatdongcongdong.entity.ChucDanh;
import com.quanly.hoatdongcongdong.entity.TaiKhoan;

import java.time.Year;
import java.util.Date;

public class ThongTinTaiKhoanResponse {
    private Long maTaiKhoan;
    private String tenDangNhap;
    private String tenDayDu;
    private String diaChi;
    private TaiKhoan.GioiTinh gioiTinh;
    private String soDienThoai;
    private Date ngaySinh;
    // Các trường thông tin chung khác

    private Year namNhapHoc; // Chỉ có trong trường hợp SinhVien
    private ChucDanh chucDanh; // Chỉ có trong trường hợp GiangVien


    public ThongTinTaiKhoanResponse() {
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public TaiKhoan.GioiTinh getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(TaiKhoan.GioiTinh gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public Long getMaTaiKhoan() {
        return maTaiKhoan;
    }

    public void setMaTaiKhoan(Long maTaiKhoan) {
        this.maTaiKhoan = maTaiKhoan;
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public String getTenDayDu() {
        return tenDayDu;
    }

    public void setTenDayDu(String tenDayDu) {
        this.tenDayDu = tenDayDu;
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

    public Year getNamNhapHoc() {
        return namNhapHoc;
    }

    public void setNamNhapHoc(Year namNhapHoc) {
        this.namNhapHoc = namNhapHoc;
    }

    public ChucDanh getChucDanh() {
        return chucDanh;
    }

    public void setChucDanh(ChucDanh chucDanh) {
        this.chucDanh = chucDanh;
    }


}

