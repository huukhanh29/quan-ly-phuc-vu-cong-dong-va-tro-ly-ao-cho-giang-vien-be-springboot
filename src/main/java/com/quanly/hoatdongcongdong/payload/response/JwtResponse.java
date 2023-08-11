package com.quanly.hoatdongcongdong.payload.response;

public class JwtResponse {
  private String token;
  private String refreshToken;
  private String type = "Bearer";

  public JwtResponse() {
  }

  public JwtResponse(String token) {
    this.token = token;
  }
  public JwtResponse(String token, String refreshToken) {
    this.refreshToken =refreshToken;
    this.token = token;
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
