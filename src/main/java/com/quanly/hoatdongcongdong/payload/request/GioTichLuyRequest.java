package com.quanly.hoatdongcongdong.payload.request;

public class GioTichLuyRequest {

    private Long maGioTichLuy;
    private Long maGiangVien; // Thay vì dùng trực tiếp đối tượng GiangVien
    private int tongSoGio;
    private int gioMienGiam;
    private String nam;

    public Long getMaGioTichLuy() {
        return maGioTichLuy;
    }

    public void setMaGioTichLuy(Long maGioTichLuy) {
        this.maGioTichLuy = maGioTichLuy;
    }

    public Long getMaGiangVien() {
        return maGiangVien;
    }

    public void setMaGiangVien(Long maGiangVien) {
        this.maGiangVien = maGiangVien;
    }

    public int getTongSoGio() {
        return tongSoGio;
    }

    public void setTongSoGio(int tongSoGio) {
        this.tongSoGio = tongSoGio;
    }

    public int getGioMienGiam() {
        return gioMienGiam;
    }

    public void setGioMienGiam(int gioMienGiam) {
        this.gioMienGiam = gioMienGiam;
    }

    public String getNam() {
        return nam;
    }

    public void setNam(String nam) {
        this.nam = nam;
    }
}
