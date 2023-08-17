package com.quanly.hoatdongcongdong.payload.request;

import jakarta.persistence.Column;

public class CauHoiRequest {
    @Column(columnDefinition = "TEXT")
    private String cauHoi;

    public CauHoiRequest(String cauHoi) {
        this.cauHoi = cauHoi;
    }
    public CauHoiRequest() {
        this.cauHoi ="";
    }
    public String getCauHoi() {
        return cauHoi;
    }

    public void setCauHoi(String cauHoi) {
        this.cauHoi = cauHoi;
    }
}

