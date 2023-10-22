package com.quanly.hoatdongcongdong.entity;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    private int gioTichLuyThamGia;
    private int gioTichLuyToChuc;
    private LocalDateTime thoiGianBatDau;
    private LocalDateTime thoiGianKetThuc;

    private String tenQuyetDinh;
    private String soQuyetDinh;
    private String nguoiKyQuyetDinh;
    @Column(columnDefinition = "TEXT")
    private String fileQuyetDinh;
    public enum CapToChuc {
        KHOA, TRUONG, BOMON, CANHAN
    }

    @Enumerated(EnumType.STRING)
    private CapToChuc capToChuc;
    @ManyToOne
    @JoinColumn(name = "maLoaiHoatDong")
    private LoaiHoatDong loaiHoatDong;
    @ManyToMany
    @JoinTable(
            name = "ToChuc",
            joinColumns = @JoinColumn(name = "maHoatDong"),
            inverseJoinColumns = @JoinColumn(name = "maGiangVien")
    )
    private List<GiangVien> giangVienToChucs = new ArrayList<>();
    @CreationTimestamp
    private LocalDateTime ngayTao;
    @UpdateTimestamp
    private LocalDateTime ngayCapNhat;
    @Formula("(CASE WHEN thoi_gian_bat_dau > now() THEN 'SAP_DIEN_RA' WHEN thoi_gian_ket_thuc > now() THEN 'DANG_DIEN_RA' ELSE 'DA_DIEN_RA' END)")
    @Enumerated(EnumType.STRING)
    private TrangThaiHoatDong trangThaiHoatDong;
    public enum TrangThaiHoatDong {
        SAP_DIEN_RA, DANG_DIEN_RA, DA_DIEN_RA
    }
    public TrangThaiHoatDong getTrangThaiHoatDong() {
        return trangThaiHoatDong;
    }

    public void setTrangThaiHoatDong(TrangThaiHoatDong trangThaiHoatDong) {
        this.trangThaiHoatDong = trangThaiHoatDong;
    }

    public HoatDong() {
    }

    public HoatDong(Long maHoatDong, String tenHoatDong, String moTa,
                    String diaDiem, int gioTichLuyThamGia,
                    int gioTichLuyToChuc, LocalDateTime thoiGianBatDau,
                    LocalDateTime thoiGianKetThuc, String tenQuyetDinh,
                    String soQuyetDinh, String nguoiKyQuyetDinh, String fileQuyetDinh,
                    CapToChuc capToChuc, LoaiHoatDong loaiHoatDong, List<GiangVien> giangVienToChucs,
                    LocalDateTime ngayTao, LocalDateTime ngayCapNhat) {
        this.maHoatDong = maHoatDong;
        this.tenHoatDong = tenHoatDong;
        this.moTa = moTa;
        this.diaDiem = diaDiem;
        this.gioTichLuyThamGia = gioTichLuyThamGia;
        this.gioTichLuyToChuc = gioTichLuyToChuc;
        this.thoiGianBatDau = thoiGianBatDau;
        this.thoiGianKetThuc = thoiGianKetThuc;
        this.tenQuyetDinh = tenQuyetDinh;
        this.soQuyetDinh = soQuyetDinh;
        this.nguoiKyQuyetDinh = nguoiKyQuyetDinh;
        this.fileQuyetDinh = fileQuyetDinh;
        this.capToChuc = capToChuc;
        this.loaiHoatDong = loaiHoatDong;
        this.giangVienToChucs = giangVienToChucs;
        this.ngayTao = ngayTao;
        this.ngayCapNhat = ngayCapNhat;
    }

    public HoatDong(String tenHoatDong, String moTa,
                    String diaDiem,
                    int gioTichLuyThamGia, int gioTichLuyToChuc,
                    LocalDateTime thoiGianBatDau,
                    LocalDateTime thoiGianKetThuc) {
        this.tenHoatDong = tenHoatDong;
        this.moTa = moTa;
        this.diaDiem = diaDiem;
        this.gioTichLuyThamGia = gioTichLuyThamGia;
        this.gioTichLuyToChuc = gioTichLuyToChuc;
        this.thoiGianBatDau = thoiGianBatDau;
        this.thoiGianKetThuc = thoiGianKetThuc;
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

    public CapToChuc getCapToChuc() {
        return capToChuc;
    }

    public void setCapToChuc(CapToChuc capToChuc) {
        this.capToChuc = capToChuc;
    }

    public List<GiangVien> getGiangVienToChucs() {
        return giangVienToChucs;
    }

    public void setGiangVienToChucs(List<GiangVien> giangVienToChucs) {
        this.giangVienToChucs = giangVienToChucs;
    }

}
