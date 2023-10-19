package com.quanly.hoatdongcongdong.entity;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
public class HoatDongNgoaiTruong {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maHoatDongNgoaiTruong;

    @ManyToOne
    @JoinColumn(name = "maGiangVien")
    private GiangVien giangVien;

    private String tenHoatDong;
    private String banToChuc;
    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String moTa;

    private String diaDiem;

    private int gioTichLuyThamGia;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime thoiGianBatDau;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime thoiGianKetThuc;
    private String fileMinhChung;
    @Enumerated(EnumType.STRING)
    private TrangThai trangThai;
    public enum TrangThai {
        Chua_Duyet, Da_Duyet
    }
    @CreationTimestamp
    private LocalDateTime ngayTao;
    @UpdateTimestamp
    private LocalDateTime ngayCapNhat;

    public HoatDongNgoaiTruong() {
    }

    public HoatDongNgoaiTruong(Long maHoatDongNgoaiTruong, GiangVien giangVien, String tenHoatDong, String banToChuc,
                               String moTa, String diaDiem, int gioTichLuyThamGia,
                               LocalDateTime thoiGianBatDau, LocalDateTime thoiGianKetThuc,
                               String fileMinhChung, LocalDateTime ngayTao,
                               LocalDateTime ngayCapNhat, TrangThai trangThai) {
        this.maHoatDongNgoaiTruong = maHoatDongNgoaiTruong;
        this.giangVien = giangVien;
        this.tenHoatDong = tenHoatDong;
        this.banToChuc = banToChuc;
        this.moTa = moTa;
        this.diaDiem = diaDiem;
        this.gioTichLuyThamGia = gioTichLuyThamGia;
        this.thoiGianBatDau = thoiGianBatDau;
        this.thoiGianKetThuc = thoiGianKetThuc;
        this.fileMinhChung = fileMinhChung;
        this.ngayTao = ngayTao;
        this.ngayCapNhat = ngayCapNhat;
        this.trangThai = trangThai;
    }

    public TrangThai getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThai trangThai) {
        this.trangThai = trangThai;
    }

    public Long getMaHoatDongNgoaiTruong() {
        return maHoatDongNgoaiTruong;
    }

    public void setMaHoatDongNgoaiTruong(Long maHoatDongNgoaiTruong) {
        this.maHoatDongNgoaiTruong = maHoatDongNgoaiTruong;
    }

    public GiangVien getGiangVien() {
        return giangVien;
    }

    public void setGiangVien(GiangVien giangVien) {
        this.giangVien = giangVien;
    }

    public String getTenHoatDong() {
        return tenHoatDong;
    }

    public void setTenHoatDong(String tenHoatDong) {
        this.tenHoatDong = tenHoatDong;
    }

    public String getBanToChuc() {
        return banToChuc;
    }

    public void setBanToChuc(String banToChuc) {
        this.banToChuc = banToChuc;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getDiaDiem() {
        return diaDiem;
    }

    public void setDiaDiem(String diaDiem) {
        this.diaDiem = diaDiem;
    }

    public int getGioTichLuyThamGia() {
        return gioTichLuyThamGia;
    }

    public void setGioTichLuyThamGia(int gioTichLuyThamGia) {
        this.gioTichLuyThamGia = gioTichLuyThamGia;
    }

    public LocalDateTime getThoiGianBatDau() {
        return thoiGianBatDau;
    }

    public void setThoiGianBatDau(LocalDateTime thoiGianBatDau) {
        this.thoiGianBatDau = thoiGianBatDau;
    }

    public LocalDateTime getThoiGianKetThuc() {
        return thoiGianKetThuc;
    }

    public void setThoiGianKetThuc(LocalDateTime thoiGianKetThuc) {
        this.thoiGianKetThuc = thoiGianKetThuc;
    }

    public String getFileMinhChung() {
        return fileMinhChung;
    }

    public void setFileMinhChung(String fileMinhChung) {
        this.fileMinhChung = fileMinhChung;
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
}
