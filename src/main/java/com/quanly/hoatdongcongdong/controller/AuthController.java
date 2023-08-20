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
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.validation.ObjectError;


@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("/tai-khoan")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final TaiKhoanService taiKhoanService;
    private final ChucDanhService chucDanhService;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtils jwtUtils;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager,
                               TaiKhoanService taiKhoanService,
                               ChucDanhService chucDanhService,
                               RefreshTokenService refreshTokenService,
                               JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.taiKhoanService = taiKhoanService;
        this.chucDanhService = chucDanhService;
        this.refreshTokenService = refreshTokenService;
        this.jwtUtils = jwtUtils;
    }


    @PostMapping("/dang-nhap")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        String taiKhoan = loginRequest.getTaiKhoan();

        if (taiKhoanService.isTaiKhoanKhoa(taiKhoan)) {
            throw new BadRequestException("Tài khoản bị khóa");
        }

        // Kiểm tra thông tin
        Optional<TaiKhoan> taiKhoanInfo = taiKhoanService.findByTenDangNhap(taiKhoan);
        if (taiKhoanInfo.isEmpty() ||
                !taiKhoanService.isMatKhauHopLe(loginRequest.getMatKhau(), taiKhoanInfo.get().getMatKhau()) ||
                !taiKhoanService.existsByTenDangNhap(taiKhoan)
        ) {
            throw new BadRequestException("Sai thông tin đăng nhập");
        }

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getTaiKhoan(), loginRequest.getMatKhau()));
        } catch (AuthenticationException e) {
            throw new UnAuthorizeException("Lỗi xác thực");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        JwtResponse response = new JwtResponse(jwt, refreshToken.getToken(), userDetails.getUsername(), userDetails.getAuthority().getAuthority());
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
                    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                    JwtResponse response = new JwtResponse(jwt, requestRefreshToken, userDetails.getUsername(), userDetails.getAuthority().getAuthority());
                    return ResponseEntity.ok(response);
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken, "Refresh token is not in database!"));
    }


    @PostMapping("/dang-xuat")
    public ResponseEntity<?> logoutUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getId();
        refreshTokenService.deleteByMaTaiKhoan(userId);
        return ResponseEntity.ok(new MessageResponse("Log out successful!"));
    }

    @PostMapping("/them-tai-khoan")
    public ResponseEntity<?> themNguoiDung(@Valid @RequestBody TaiKhoanMoiRequest request) {
        if (request.getQuyen() == TaiKhoan.Quyen.SinhVien) {
            taiKhoanService.themMoiSinhVien(request.getTenDangNhap(), request.getMatKhau(),
                    request.getEmail(), request.getQuyen(), request.getTenDayDu(),
                    request.getGioiTinh(), request.getNamNhapHoc());
        } else if (request.getQuyen() == TaiKhoan.Quyen.GiangVien) {
            Optional<ChucDanh> chucDanh = chucDanhService.findById(request.getMaChucDanh());
            if (chucDanh.isEmpty()) {
                throw new EntityNotFoundException("Không tìm thấy chức danh!");
            }
            taiKhoanService.themMoiGiangVien(request.getTenDangNhap(), request.getMatKhau(),
                    request.getEmail(), request.getQuyen(), request.getTenDayDu(),
                    request.getGioiTinh(), chucDanh.get());
        } else {
            throw new EntityNotFoundException("Quyền tài khoản không hợp lệ");
        }
        return ResponseEntity.ok(new MessageResponse("Thêm tài khoản thành công!"));
    }

}
