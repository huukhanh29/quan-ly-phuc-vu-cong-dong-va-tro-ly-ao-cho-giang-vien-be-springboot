package com.quanly.hoatdongcongdong.controller;

import com.quanly.hoatdongcongdong.entity.ChucDanh;
import com.quanly.hoatdongcongdong.entity.TaiKhoan;
import com.quanly.hoatdongcongdong.entity.RefreshToken;
import com.quanly.hoatdongcongdong.exception.BadRequestException;
import com.quanly.hoatdongcongdong.exception.TokenRefreshException;
import com.quanly.hoatdongcongdong.exception.UnAuthorizeException;
import com.quanly.hoatdongcongdong.payload.request.LoginRequest;
import com.quanly.hoatdongcongdong.payload.request.TaiKhoanMoiRequest;
import com.quanly.hoatdongcongdong.payload.request.TokenRefreshRequest;
import com.quanly.hoatdongcongdong.payload.request.TokenRequest;
import com.quanly.hoatdongcongdong.payload.response.JwtResponse;
import com.quanly.hoatdongcongdong.payload.response.MessageResponse;
import com.quanly.hoatdongcongdong.sercurity.jwt.JwtUtils;
import com.quanly.hoatdongcongdong.service.ChucDanhService;
import com.quanly.hoatdongcongdong.service.RefreshTokenService;
import com.quanly.hoatdongcongdong.service.TaiKhoanService;
import com.quanly.hoatdongcongdong.service.UserDetailsImpl;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/tai-khoan")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final TaiKhoanService taiKhoanService;

    private final RefreshTokenService refreshTokenService;
    private final JwtUtils jwtUtils;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager,
                          TaiKhoanService taiKhoanService,
                          RefreshTokenService refreshTokenService,
                          JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.taiKhoanService = taiKhoanService;
        this.refreshTokenService = refreshTokenService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/kiem-tra-dang-nhap")
    public ResponseEntity<?> testLogin(@RequestBody TokenRequest token) {
        if (token.getToken() != null && !token.getToken().isEmpty()) {
            if (JwtUtils.testJwtToken(token.getToken())) {
                return ResponseEntity.ok(new MessageResponse("ok"));
            }else{
                return new ResponseEntity<>(new MessageResponse("error"), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(new MessageResponse("empty"), HttpStatus.OK);
    }

    @PostMapping("/dang-nhap")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        String taiKhoan = loginRequest.getTaiKhoan();

        if (taiKhoanService.isTaiKhoanKhoa(taiKhoan)) {
            return new ResponseEntity<>(new MessageResponse("account-block"), HttpStatus.OK);
        }

        // Kiểm tra thông tin
        Optional<TaiKhoan> taiKhoanInfo = taiKhoanService.findByTenDangNhap(taiKhoan);
        if (taiKhoanInfo.isEmpty() ||
                !taiKhoanService.isMatKhauHopLe(loginRequest.getMatKhau(), taiKhoanInfo.get().getMatKhau()) ||
                !taiKhoanService.existsByTenDangNhap(taiKhoan)
        ) {
            return new ResponseEntity<>(new MessageResponse("info-warning"), HttpStatus.OK);
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getTaiKhoan(), loginRequest.getMatKhau()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        Long expirationDate = jwtUtils.getExpirationDateFromJwtToken(jwt).getTime() / 1000;
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        JwtResponse response = new JwtResponse(jwt, refreshToken.getRefreshtoken(), userDetails.getUsername(), userDetails.getAuthority().getAuthority(), expirationDate);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/cap-lai-token")
    public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getTaiKhoan)
                .map(user -> {
                    Authentication authentication = jwtUtils.getAuthenticationFromUser(user);
                    String jwt = jwtUtils.generateJwtToken(authentication);
                    Long expirationDate = jwtUtils.getExpirationDateFromJwtToken(jwt).getTime() / 1000;
                    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                    JwtResponse response = new JwtResponse(jwt, requestRefreshToken, userDetails.getUsername(), userDetails.getAuthority().getAuthority(), expirationDate);
                    return ResponseEntity.ok(response);
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken, "Refresh token is not in database!"));
    }


}
