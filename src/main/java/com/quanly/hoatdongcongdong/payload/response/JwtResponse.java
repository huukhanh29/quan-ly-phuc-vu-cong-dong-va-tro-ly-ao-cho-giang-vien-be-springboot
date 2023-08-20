package com.quanly.hoatdongcongdong.payload.response;

public class JwtResponse {
  private String token;
  private String type = "Bearer";
  private String tenTaiKhoan;
  private String quyen;
  private String refreshToken;

  public JwtResponse() {
  }

  public JwtResponse(String token, String refreshToken, String tenTaiKhoan, String quyen) {
    this.token = token;
    this.refreshToken = refreshToken;
    this.tenTaiKhoan = tenTaiKhoan;
    this.quyen = quyen;
  }

  public String getTenTaiKhoan() {
    return tenTaiKhoan;
  }

  public void setTenTaiKhoan(String tenTaiKhoan) {
    this.tenTaiKhoan = tenTaiKhoan;
  }

  public String getQuyen() {
    return quyen;
  }

  public void setQuyen(String quyen) {
    this.quyen = quyen;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
