package com.quanly.hoatdongcongdong.entity;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
public class CauHoi{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maCauHoi;
    @Column(columnDefinition = "TEXT")
    private String cauHoi;

    @Column(columnDefinition = "TEXT")
    private String traLoi;

    @CreationTimestamp
    private LocalDateTime ngayTao;

    @UpdateTimestamp
    private LocalDateTime ngayCapNhat;
    @Formula("(SELECT COUNT(DISTINCT h.ma_sinh_vien) FROM lich_su h WHERE h.ma_cau_hoi= ma_cau_hoi)")
    private int soLuongDaHoi;
    public int getSoLuongDaHoi() {
        return soLuongDaHoi;
    }
    public void setSoLuongDaHoi(int soLuongDaHoi) {
        this.soLuongDaHoi = soLuongDaHoi;
    }
//    @Formula("(SELECT COUNT(*) - 1 FROM cau_hoi c WHERE CALCULATE_SIMILARITY(c.cau_hoi, cau_hoi) > 0.7)")
//    private int soLuongCauHoiTuongTu;
//
//    public int getSoLuongCauHoiTuongTu() {
//        return soLuongCauHoiTuongTu;
//    }
//
//    public void setSoLuongCauHoiTuongTu(int soLuongCauHoiTuongTu) {
//        this.soLuongCauHoiTuongTu = soLuongCauHoiTuongTu;
//    }

    public CauHoi(Long maCauHoi, String cauHoi, String traLoi, LocalDateTime ngayTao, LocalDateTime ngayCapNhat) {
        this.maCauHoi = maCauHoi;
        this.cauHoi = cauHoi;
        this.traLoi = traLoi;
        this.ngayTao = ngayTao;
        this.ngayCapNhat = ngayCapNhat;
    }

    public CauHoi() {
    }

    public Long getMaCauHoi() {
        return maCauHoi;
    }

    public void setMaCauHoi(Long maCauHoi) {
        this.maCauHoi = maCauHoi;
    }

    public String getCauHoi() {
        return cauHoi;
    }

    public void setCauHoi(String cauHoi) {
        this.cauHoi = cauHoi;
    }

    public String getTraLoi() {
        return traLoi;
    }

    public void setTraLoi(String traLoi) {
        this.traLoi = traLoi;
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

