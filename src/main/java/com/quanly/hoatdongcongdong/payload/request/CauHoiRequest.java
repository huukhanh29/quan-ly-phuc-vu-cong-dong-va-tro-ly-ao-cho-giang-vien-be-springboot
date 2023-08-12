package com.quanly.hoatdongcongdong.payload.request;

public class CauHoiRequest {
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

