package com.quanly.hoatdongcongdong.entity;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "HoatDong")
public class HoatDong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maHoatDong;

    private String tenHoatDong;
    @Column(columnDefinition = "TEXT")
    private String moTa;
    private String diaDiem;
    @ManyToOne
    @JoinColumn(name = "maGiangVienDeXuat")
    private GiangVien giangVienDeXuat;
    private int gioTichLuyThamGia;
    private int gioTichLuyToChuc;
    private LocalDateTime thoiGianBatDau;
    private LocalDateTime thoiGianKetThuc;
    public enum TrangThaiHoatDong {
        ChuaDuyet, ChuaDienRa, DaDienRa
    }

    @Enumerated(EnumType.STRING)
    private TrangThaiHoatDong trangThai;

    private String tenQuyetDinh;
    private String soQuyetDinh;
    private String nguoiKyQuyetDinh;
    private String fileQuyetDinh;

    @ManyToOne
    @JoinColumn(name = "maLoaiHoatDong")
    private LoaiHoatDong loaiHoatDong;

    @CreationTimestamp
    private LocalDateTime ngayTao;
    @UpdateTimestamp
    private LocalDateTime ngayCapNhat;

    public HoatDong() {
    }

    public HoatDong(Long maHoatDong, String tenHoatDong, String moTa,
                    String diaDiem, GiangVien giangVienDeXuat,
                    int gioTichLuyThamGia, int gioTichLuyToChuc,
                    LocalDateTime thoiGianBatDau,
                    LocalDateTime thoiGianKetThuc, TrangThaiHoatDong trangThai,
                    String tenQuyetDinh, String soQuyetDinh, String nguoiKyQuyetDinh,
                    String fileQuyetDinh, LoaiHoatDong loaiHoatDong,
                    LocalDateTime ngayTao, LocalDateTime ngayCapNhat) {
        this.maHoatDong = maHoatDong;
        this.tenHoatDong = tenHoatDong;
        this.moTa = moTa;
        this.diaDiem = diaDiem;
        this.giangVienDeXuat = giangVienDeXuat;
        this.gioTichLuyThamGia = gioTichLuyThamGia;
        this.gioTichLuyToChuc = gioTichLuyToChuc;
        this.thoiGianBatDau = thoiGianBatDau;
        this.thoiGianKetThuc = thoiGianKetThuc;
        this.trangThai = trangThai;
        this.tenQuyetDinh = tenQuyetDinh;
        this.soQuyetDinh = soQuyetDinh;
        this.nguoiKyQuyetDinh = nguoiKyQuyetDinh;
        this.fileQuyetDinh = fileQuyetDinh;
        this.loaiHoatDong = loaiHoatDong;
        this.ngayTao = ngayTao;
        this.ngayCapNhat = ngayCapNhat;
    }

    public Long getMaHoatDong() {
        return maHoatDong;
    }

    public void setMaHoatDong(Long maHoatDong) {
        this.maHoatDong = maHoatDong;
    }

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

    public GiangVien getGiangVienDeXuat() {
        return giangVienDeXuat;
    }

    public void setGiangVienDeXuat(GiangVien giangVienDeXuat) {
        this.giangVienDeXuat = giangVienDeXuat;
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

    public TrangThaiHoatDong getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiHoatDong trangThai) {
        this.trangThai = trangThai;
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

    public LoaiHoatDong getLoaiHoatDong() {
        return loaiHoatDong;
    }

    public void setLoaiHoatDong(LoaiHoatDong loaiHoatDong) {
        this.loaiHoatDong = loaiHoatDong;
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
