package com.quanly.hoatdongcongdong.payload.request;

import jakarta.persistence.Column;

public class HuyHoatDongRequest {
    @Column(columnDefinition = "TEXT")
    private String lyDoHuy;

    public String getLyDoHuy() {
        return lyDoHuy;
    }
    public void setLyDoHuy(String lyDoHuy) {
        this.lyDoHuy = lyDoHuy;
    }
}
