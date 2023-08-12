package com.quanly.hoatdongcongdong.payload.response;


import com.quanly.hoatdongcongdong.entity.CauHoi;
import com.quanly.hoatdongcongdong.entity.PhanHoi;
import com.quanly.hoatdongcongdong.entity.SinhVien;
import com.quanly.hoatdongcongdong.entity.TaiKhoan;

import java.time.LocalDateTime;

public class PhanHoiResponse {
    private Long maPhanHoi;
    private String noiDung;
    private String cauHoi;
    private String phanHoi;

    public String getPhanHoi() {
        return phanHoi;
    }

    public void setPhanHoi(String phanHoi) {
        this.phanHoi = phanHoi;
    }

    private String tenTaiKhoan;

    public String getTenTaiKhoan() {
        return tenTaiKhoan;
    }

    public void setTenTaiKhoan(String tenTaiKhoan) {
        this.tenTaiKhoan = tenTaiKhoan;
    }

    public PhanHoiResponse(Long maPhanHoi, String noiDung, String cauHoi,
                           String phanHoi, String tenTaiKhoan, LocalDateTime ngayTao,
                           LocalDateTime ngayCapNhat) {
        this.maPhanHoi = maPhanHoi;
        this.noiDung = noiDung;
        this.cauHoi = cauHoi;
        this.phanHoi = phanHoi;
        this.tenTaiKhoan = tenTaiKhoan;
        this.ngayTao = ngayTao;
        this.ngayCapNhat = ngayCapNhat;
    }

    private LocalDateTime ngayTao;
    private LocalDateTime ngayCapNhat;

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

    public String getCauHoi() {
        return cauHoi;
    }

    public void setCauHoi(String cauHoi) {
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

    public static PhanHoiResponse fromEntity(PhanHoi phanHoiEntity) {
        CauHoi cauHoi = phanHoiEntity.getCauHoi();
        SinhVien sinhVien = phanHoiEntity.getSinhVien();
        TaiKhoan taiKhoan = sinhVien.getTaiKhoan();
        String tenTaiKhoan = taiKhoan != null ? taiKhoan.getTenDayDu() : null;
        return new PhanHoiResponse(
                phanHoiEntity.getMaPhanHoi(),
                phanHoiEntity.getNoiDung(),
                cauHoi != null ? cauHoi.getCauHoi() : null,
                cauHoi != null ? cauHoi.getTraLoi() : null,
                tenTaiKhoan,
                phanHoiEntity.getNgayTao(),
                phanHoiEntity.getNgayCapNhat()
        );
    }
}
