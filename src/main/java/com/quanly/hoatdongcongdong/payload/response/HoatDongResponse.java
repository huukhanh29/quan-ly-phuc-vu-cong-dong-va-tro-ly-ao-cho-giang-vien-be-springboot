package com.quanly.hoatdongcongdong.payload.response;

import com.quanly.hoatdongcongdong.entity.HoatDong;

import java.time.LocalDateTime;
import java.util.List;

public class HoatDongResponse {

    private String tenHoatDong;
    private String moTa;
    private String diaDiem;
    private int gioTichLuyThamGia;
    private int gioTichLuyToChuc;
    private LocalDateTime thoiGianBatDau;
    private LocalDateTime thoiGianKetThuc;
    private String tenQuyetDinh;
    private String soQuyetDinh;
    private String nguoiKyQuyetDinh;
    private String fileQuyetDinh;
    private HoatDong.CapToChuc capToChuc;
    private Long maLoaiHoatDong;
    private List<Long> maGiangVienToChucs;

    public String getTenHoatDong() {
        return tenHoatDong;
    }

    public void setTenHoatDong(String tenHoatDong) {
        this.tenHoatDong = tenHoatDong;
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

    public int getGioTichLuyToChuc() {
        return gioTichLuyToChuc;
    }

    public void setGioTichLuyToChuc(int gioTichLuyToChuc) {
        this.gioTichLuyToChuc = gioTichLuyToChuc;
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

    public String getTenQuyetDinh() {
        return tenQuyetDinh;
    }

    public void setTenQuyetDinh(String tenQuyetDinh) {
        this.tenQuyetDinh = tenQuyetDinh;
    }

    public String getSoQuyetDinh() {
        return soQuyetDinh;
    }

    public void setSoQuyetDinh(String soQuyetDinh) {
        this.soQuyetDinh = soQuyetDinh;
    }

    public String getNguoiKyQuyetDinh() {
        return nguoiKyQuyetDinh;
    }

    public void setNguoiKyQuyetDinh(String nguoiKyQuyetDinh) {
        this.nguoiKyQuyetDinh = nguoiKyQuyetDinh;
    }

    public String getFileQuyetDinh() {
        return fileQuyetDinh;
    }

    public void setFileQuyetDinh(String fileQuyetDinh) {
        this.fileQuyetDinh = fileQuyetDinh;
    }

    public HoatDong.CapToChuc getCapToChuc() {
        return capToChuc;
    }

    public void setCapToChuc(HoatDong.CapToChuc capToChuc) {
        this.capToChuc = capToChuc;
    }

    public Long getMaLoaiHoatDong() {
        return maLoaiHoatDong;
    }

    public void setMaLoaiHoatDong(Long maLoaiHoatDong) {
        this.maLoaiHoatDong = maLoaiHoatDong;
    }

    public List<Long> getMaGiangVienToChucs() {
        return maGiangVienToChucs;
    }

    public void setMaGiangVienToChucs(List<Long> maGiangVienToChucs) {
        this.maGiangVienToChucs = maGiangVienToChucs;
    }
}

