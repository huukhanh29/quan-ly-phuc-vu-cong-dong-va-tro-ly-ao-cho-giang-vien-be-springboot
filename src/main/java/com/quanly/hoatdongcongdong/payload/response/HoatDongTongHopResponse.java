package com.quanly.hoatdongcongdong.payload.response;

import java.time.LocalDateTime;
import java.util.List;

public class HoatDongTongHopResponse {
    private List<HoatDongDTO> danhSachHoatDong;
    private int tongSoGio;
    private int gioBatBuoc;
    private int gioHk1;
    private int gioHk2;
    private int gioHk3;
    private int gioVuotMuc;
    private int gioMienGiam;
    private int gioThieu;

    public int getGioThieu() {
        return gioThieu;
    }

    public void setGioThieu(int gioThieu) {
        this.gioThieu = gioThieu;
    }

    public int getGioVuotMuc() {
        return gioVuotMuc;
    }

    public void setGioVuotMuc(int gioVuotMuc) {
        this.gioVuotMuc = gioVuotMuc;
    }

    public int getGioMienGiam() {
        return gioMienGiam;
    }

    public void setGioMienGiam(int gioMienGiam) {
        this.gioMienGiam = gioMienGiam;
    }

    public int getGioHk1() {
        return gioHk1;
    }

    public void setGioHk1(int gioHk1) {
        this.gioHk1 = gioHk1;
    }

    public int getGioHk2() {
        return gioHk2;
    }

    public void setGioHk2(int gioHk2) {
        this.gioHk2 = gioHk2;
    }

    public int getGioHk3() {
        return gioHk3;
    }

    public void setGioHk3(int gioHk3) {
        this.gioHk3 = gioHk3;
    }

    public List<HoatDongDTO> getDanhSachHoatDong() {
        return danhSachHoatDong;
    }

    public void setDanhSachHoatDong(List<HoatDongDTO> danhSachHoatDong) {
        this.danhSachHoatDong = danhSachHoatDong;
    }

    public int getTongSoGio() {
        return tongSoGio;
    }

    public void setTongSoGio(int tongSoGio) {
        this.tongSoGio = tongSoGio;
    }

    public int getGioBatBuoc() {
        return gioBatBuoc;
    }

    public void setGioBatBuoc(int gioBatBuoc) {
        this.gioBatBuoc = gioBatBuoc;
    }
}