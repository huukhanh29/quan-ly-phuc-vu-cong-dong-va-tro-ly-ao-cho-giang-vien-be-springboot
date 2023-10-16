package com.quanly.hoatdongcongdong.payload.request;

import com.quanly.hoatdongcongdong.entity.HoatDongNgoaiTruong;

import java.time.LocalDateTime;

public class HoatDongNgoaiTruongRequest {

    private Long maGiangVien;
    private String tenHoatDong;
    private String banToChuc;
    private String moTa;
    private String diaDiem;
    private int gioTichLuyThamGia;
    private LocalDateTime thoiGianBatDau;
    private LocalDateTime thoiGianKetThuc;
    private String fileMinhChung;
    private HoatDongNgoaiTruong.TrangThai trangThai;

    public Long getMaGiangVien() {
        return maGiangVien;
    }

    public void setMaGiangVien(Long maGiangVien) {
        this.maGiangVien = maGiangVien;
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

    public HoatDongNgoaiTruong.TrangThai getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(HoatDongNgoaiTruong.TrangThai trangThai) {
        this.trangThai = trangThai;
    }
}
