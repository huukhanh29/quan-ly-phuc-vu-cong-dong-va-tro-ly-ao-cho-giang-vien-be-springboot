package com.quanly.hoatdongcongdong.payload.response;


import com.quanly.hoatdongcongdong.entity.CauHoi;
import com.quanly.hoatdongcongdong.entity.PhanHoi;
import com.quanly.hoatdongcongdong.entity.SinhVien;
import com.quanly.hoatdongcongdong.entity.TaiKhoan;

import java.time.LocalDateTime;

public class PhanHoiResponse {
    private Long maPhanHoi;
    private String noiDung;
    private String tenTaiKhoan;

    public String getTenTaiKhoan() {
        return tenTaiKhoan;
    }

    public void setTenTaiKhoan(String tenTaiKhoan) {
        this.tenTaiKhoan = tenTaiKhoan;
    }

    public PhanHoiResponse(Long maPhanHoi, String noiDung,
                           String tenTaiKhoan, LocalDateTime ngayTao, LocalDateTime ngayCapNhat) {
        this.maPhanHoi = maPhanHoi;
        this.noiDung = noiDung;
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
