package com.quanly.hoatdongcongdong.entity;
import jakarta.persistence.*;
import org.hibernate.annotations.*;

import java.time.LocalDateTime;

@Entity
public class LichSu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maLichSu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maSinhVien", nullable = false)
    private SinhVien sinhVien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maCauHoi", nullable = false)
    private CauHoi cauHoi;

    @CreationTimestamp
    private LocalDateTime ngayTao;
    @UpdateTimestamp
    private LocalDateTime ngayCapNhat;

    public LichSu() {
    }

    public LichSu(Long maLichSu, SinhVien sinhVien, CauHoi cauHoi,
                  LocalDateTime ngayTao, LocalDateTime ngayCapNhat) {
        this.maLichSu = maLichSu;
        this.sinhVien = sinhVien;
        this.cauHoi = cauHoi;
        this.ngayTao = ngayTao;
        this.ngayCapNhat = ngayCapNhat;
    }

    public Long getMaLichSu() {
        return maLichSu;
    }

    public void setMaLichSu(Long maLichSu) {
        this.maLichSu = maLichSu;
    }

    public SinhVien getSinhVien() {
        return sinhVien;
    }

    public void setSinhVien(SinhVien sinhVien) {
        this.sinhVien = sinhVien;
    }

    public CauHoi getCauHoi() {
        return cauHoi;
    }

    public void setCauHoi(CauHoi cauHoi) {
        this.cauHoi = cauHoi;
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
