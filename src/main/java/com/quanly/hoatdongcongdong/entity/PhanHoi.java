package com.quanly.hoatdongcongdong.entity;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
public class PhanHoi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maPhanHoi;
    @Column(columnDefinition = "TEXT")
    private String noiDung;

    @ManyToOne
    @JoinColumn(name = "maCauHoi", nullable = true)
    private CauHoi cauHoi;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "maSinhVien", nullable = true)
    private SinhVien sinhVien;

    @CreationTimestamp
    private LocalDateTime ngayTao;

    @UpdateTimestamp
    private LocalDateTime ngayCapNhat;

    public PhanHoi() {
    }

    public PhanHoi(Long maPhanHoi, String noiDung,
                   CauHoi cauHoi, SinhVien sinhVien, LocalDateTime ngayTao,
                   LocalDateTime ngayCapNhat) {
        this.maPhanHoi = maPhanHoi;
        this.noiDung = noiDung;
        this.cauHoi = cauHoi;
        this.sinhVien = sinhVien;
        this.ngayTao = ngayTao;
        this.ngayCapNhat = ngayCapNhat;
    }
    public PhanHoi(Long maPhanHoi, String noiDung,
                   CauHoi cauHoi, SinhVien taiKhoan) {
        this.maPhanHoi = maPhanHoi;
        this.noiDung = noiDung;
        this.cauHoi = cauHoi;
        this.sinhVien = sinhVien;
    }

    public Long getMaPhanHoi() {
        return maPhanHoi;
    }

    public void setMaPhanHoi(Long maPhanHoi) {
        this.maPhanHoi = maPhanHoi;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public CauHoi getCauHoi() {
        return cauHoi;
    }

    public void setCauHoi(CauHoi cauHoi) {
        this.cauHoi = cauHoi;
    }

    public SinhVien getSinhVien() {
        return sinhVien;
    }

    public void setSinhVien(SinhVien sinhVien) {
        this.sinhVien = sinhVien;
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
