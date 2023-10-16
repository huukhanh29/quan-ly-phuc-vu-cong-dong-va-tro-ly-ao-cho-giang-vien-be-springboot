package com.quanly.hoatdongcongdong.payload.request;

public class KhoaRequest {
    private String tenKhoa;
    private Long maTruong;

    public String getTenKhoa() {
        return tenKhoa;
    }

    public void setTenKhoa(String tenKhoa) {
        this.tenKhoa = tenKhoa;
    }

    public Long getMaTruong() {
        return maTruong;
    }

    public void setMaTruong(Long maTruong) {
        this.maTruong = maTruong;
    }
}
