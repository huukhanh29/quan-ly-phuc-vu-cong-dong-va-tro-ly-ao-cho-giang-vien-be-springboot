package com.quanly.hoatdongcongdong.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "TaiKhoan")
public class TaiKhoan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maTaiKhoan;

    @Column(nullable = false, unique = true)
    private String tenDangNhap;

    @Column(nullable = false)
    private String matKhau;

    @Column(unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Quyen quyen;
    @Column(nullable = true)
    private String anhdaidien;
    @Column(nullable = false)
    private String tenDayDu;

    private String soDienThoai;

    private Date ngaySinh;

    @Enumerated(EnumType.STRING)
    private GioiTinh gioiTinh;

    private String diaChi;
    @Enumerated(EnumType.STRING)
    private TrangThai trangthai;
    @CreationTimestamp
    @Column()
    private LocalDateTime ngayTao;
    @UpdateTimestamp
    @Column()
    private LocalDateTime ngayCapNhat;

    public TaiKhoan() {
    }

    public TaiKhoan(Long maTaiKhoan, String tenDangNhap, String matKhau,
                    String email, Quyen quyen, String anhdaidien, String tenDayDu,
                    String soDienThoai, Date ngaySinh, GioiTinh gioiTinh, String diaChi,
                    TrangThai trangthai, LocalDateTime ngayTao, LocalDateTime ngayCapNhat) {
        this.maTaiKhoan = maTaiKhoan;
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.email = email;
        this.quyen = quyen;
        this.anhdaidien = anhdaidien;
        this.tenDayDu = tenDayDu;
        this.soDienThoai = soDienThoai;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.diaChi = diaChi;
        this.trangthai = trangthai;
        this.ngayTao = ngayTao;
        this.ngayCapNhat = ngayCapNhat;
    }

    public TaiKhoan(Long maTaiKhoan, String tenDangNhap, String matKhau,
                    String email, Quyen quyen, String anhdaidien, String tenDayDu,
                    String soDienThoai, GioiTinh gioiTinh, TrangThai trangthai) {
        this.maTaiKhoan = maTaiKhoan;
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.email = email;
        this.quyen = quyen;
        this.anhdaidien = anhdaidien;
        this.tenDayDu = tenDayDu;
        this.soDienThoai = soDienThoai;
        this.gioiTinh = gioiTinh;
        this.trangthai = trangthai;
    }

    public String getAnhdaidien() {
        return anhdaidien;
    }

    public void setAnhdaidien(String anhdaidien) {
        this.anhdaidien = anhdaidien;
    }

    public TrangThai getTrangthai() {
        return trangthai;
    }

    public void setTrangthai(TrangThai trangthai) {
        this.trangthai = trangthai;
    }

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }

    public LocalDateTime getNgayCapNhat() {
        return ngayCapNhat;
    }

    public void setNgayCapNhat(LocalDateTime ngayCapNhat) {
        this.ngayCapNhat = ngayCapNhat;
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

    public Quyen getQuyen() {
        return quyen;
    }

    public void setQuyen(Quyen quyen) {
        this.quyen = quyen;
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

    public GioiTinh getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(GioiTinh gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public enum Quyen {
        QuanTriVien, GiangVien, SinhVien
    }

    public enum GioiTinh {
        Nam, Nu, Khac
    }

    public enum TrangThai {
        Mo, Khoa
    }
}