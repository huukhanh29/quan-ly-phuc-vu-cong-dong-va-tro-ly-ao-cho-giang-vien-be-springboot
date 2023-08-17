package com.quanly.hoatdongcongdong.payload.request;

import jakarta.persistence.Column;

public class PhanHoiRequest {
    @Column(columnDefinition = "TEXT")
    private String noiDung;

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }
}

