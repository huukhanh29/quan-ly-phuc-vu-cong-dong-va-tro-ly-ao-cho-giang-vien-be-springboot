package com.quanly.hoatdongcongdong.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    @Column(nullable = false)
    private String matKhau;

    @Column(unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Quyen quyen;
    @Column(nullable = false)
    private String tenDayDu;

    private String soDienThoai;

    private Date ngaySinh;

    @Enumerated(EnumType.STRING)
    private GioiTinh gioiTinh;
    @Column(columnDefinition = "TEXT")
    private String diaChi;
    @Enumerated(EnumType.STRING)
    private TrangThai trangThai;
    @CreationTimestamp
    private LocalDateTime ngayTao;
    @UpdateTimestamp
    private LocalDateTime ngayCapNhat;

    public TaiKhoan(String tenDangNhap, String matKhau, String email, Quyen quyen,
                    String tenDayDu, GioiTinh gioiTinh, String soDienThoai, Date ngaySinh,
                    String diaChi) {
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.email = email;
        this.quyen = quyen;
        this.tenDayDu = tenDayDu;
        this.gioiTinh = gioiTinh;
        this.soDienThoai =soDienThoai;
        this.ngaySinh= ngaySinh;
        this.diaChi =diaChi;
        this.trangThai = TrangThai.Mo;

    }

    public void capNhatThongTin(String soDienThoai, Date ngaySinh, GioiTinh gioiTinh, String diaChi) {
        if (soDienThoai != null) {
            this.soDienThoai = soDienThoai;
        }
        if (ngaySinh != null) {
            this.ngaySinh = ngaySinh;
        }
        if (gioiTinh != null) {
            this.gioiTinh = gioiTinh;
        }
        if (diaChi != null) {
            this.diaChi = diaChi;
        }
        this.ngayCapNhat = LocalDateTime.now();
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
    public TaiKhoan() {
    }

    public TaiKhoan(Long maTaiKhoan, String tenDangNhap, String matKhau,
                    String email, Quyen quyen, String tenDayDu,
                    String soDienThoai, Date ngaySinh, GioiTinh gioiTinh, String diaChi,
                    TrangThai trangthai, LocalDateTime ngayTao, LocalDateTime ngayCapNhat) {
        this.maTaiKhoan = maTaiKhoan;
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.email = email;
        this.quyen = quyen;

        this.tenDayDu = tenDayDu;
        this.soDienThoai = soDienThoai;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.diaChi = diaChi;
        this.trangThai = trangthai;
        this.ngayTao = ngayTao;
        this.ngayCapNhat = ngayCapNhat;
    }

    public TaiKhoan(Long maTaiKhoan, String tenDangNhap, String matKhau,
                    String email, Quyen quyen,  String tenDayDu,
                    String soDienThoai, GioiTinh gioiTinh, TrangThai trangthai) {
        this.maTaiKhoan = maTaiKhoan;
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.email = email;
        this.quyen = quyen;

        this.tenDayDu = tenDayDu;
        this.soDienThoai = soDienThoai;
        this.gioiTinh = gioiTinh;
        this.trangThai = trangthai;
    }
    public TaiKhoan(String tenDangNhap, String matKhau, String email
            , Quyen quyen, String tenDayDu, GioiTinh gioiTinh) {
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.email = email;
        this.quyen = quyen;
        this.tenDayDu = tenDayDu;
        this.gioiTinh = gioiTinh;
        this.trangThai = TrangThai.Mo;
    }



    public TrangThai getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThai trangThai) {
        this.trangThai = trangThai;
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


}