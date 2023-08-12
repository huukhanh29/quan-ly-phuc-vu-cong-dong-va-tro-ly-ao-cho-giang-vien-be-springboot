package com.quanly.hoatdongcongdong.payload.response;

import java.time.LocalDateTime;
import com.quanly.hoatdongcongdong.entity.CauHoi;
import com.quanly.hoatdongcongdong.entity.LichSu;
import com.quanly.hoatdongcongdong.entity.SinhVien;
import com.quanly.hoatdongcongdong.entity.TaiKhoan;
public class LichSuResponse {
    private Long maLichSu;
    private String cauHoi;
    private String traLoi;
    private String tenSinhVien;
    private LocalDateTime ngayTao;
    private LocalDateTime ngayCapNhat;

    public LichSuResponse(Long maLichSu, String cauHoi, String traLoi,
                          String tenSinhVien, LocalDateTime ngayTao, LocalDateTime ngayCapNhat) {
        this.maLichSu = maLichSu;
        this.cauHoi = cauHoi;
        this.traLoi = traLoi;
        this.tenSinhVien = tenSinhVien;
        this.ngayTao = ngayTao;
        this.ngayCapNhat = ngayCapNhat;
    }

    public Long getMaLichSu() {
        return maLichSu;
    }

    public void setMaLichSu(Long maLichSu) {
        this.maLichSu = maLichSu;
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

    public String getTenTaiKhoan() {
        return tenSinhVien;
    }

    public void setTenTaiKhoan(String tenSinhVien) {
        this.tenSinhVien = tenSinhVien;
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

    public static LichSuResponse fromEntity(LichSu lichSu) {
        CauHoi cauHoi = lichSu.getCauHoi();
        SinhVien sinhVien = lichSu.getSinhVien();
        TaiKhoan taiKhoan = sinhVien.getTaiKhoan();
        String tenTaiKhoan = taiKhoan != null ? taiKhoan.getTenDayDu() : null;
        return new LichSuResponse(
                lichSu.getMaLichSu(),
                cauHoi != null ? cauHoi.getCauHoi() : null,
                cauHoi != null ? cauHoi.getTraLoi() : null,
                tenTaiKhoan,
                lichSu.getNgayTao(),
                lichSu.getNgayCapNhat()
        );
    }
}
