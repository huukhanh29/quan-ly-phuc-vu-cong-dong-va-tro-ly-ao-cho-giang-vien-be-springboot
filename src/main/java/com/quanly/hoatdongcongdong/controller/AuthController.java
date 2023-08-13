package com.quanly.hoatdongcongdong.controller;

import com.quanly.hoatdongcongdong.entity.ChucDanh;
import com.quanly.hoatdongcongdong.entity.TaiKhoan;
import com.quanly.hoatdongcongdong.entity.RefreshToken;
import com.quanly.hoatdongcongdong.exception.TokenRefreshException;
import com.quanly.hoatdongcongdong.payload.request.LoginRequest;
import com.quanly.hoatdongcongdong.payload.request.SignupRequest;
import com.quanly.hoatdongcongdong.payload.request.TaiKhoanMoiRequest;
import com.quanly.hoatdongcongdong.payload.request.TokenRefreshRequest;
import com.quanly.hoatdongcongdong.payload.response.JwtResponse;
import com.quanly.hoatdongcongdong.payload.response.MessageResponse;
import com.quanly.hoatdongcongdong.payload.response.TokenRefreshResponse;
import com.quanly.hoatdongcongdong.repository.ChucDanhRepository;
import com.quanly.hoatdongcongdong.repository.TaiKhoanRepository;
import com.quanly.hoatdongcongdong.sercurity.jwt.JwtUtils;
import com.quanly.hoatdongcongdong.sercurity.services.RefreshTokenService;
import com.quanly.hoatdongcongdong.sercurity.services.TaiKhoanService;
import com.quanly.hoatdongcongdong.sercurity.services.UserDetailsImpl;
import jakarta.persistence.EntityExistsException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("/tai-khoan")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TaiKhoanService taiKhoanService;
    @Autowired
    private ChucDanhRepository chucDanhRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/dang-nhap")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        String taiKhoan = loginRequest.getTaiKhoan();
        if (!taiKhoanService.existsByTenDangNhap(taiKhoan)) {
            return new ResponseEntity<>(new MessageResponse("Sai_ten_tai_khoan"), HttpStatus.BAD_REQUEST);
        }
        if (taiKhoanService.isTaiKhoanKhoa(taiKhoan)) {
            return new ResponseEntity<>(new MessageResponse("Tai_khoan_bi_khoa"), HttpStatus.BAD_REQUEST);
        }

        // Kiểm tra mật khẩu
        Optional<TaiKhoan> taiKhoanInfo = taiKhoanService.findByTenDangNhap(taiKhoan);
        if (!taiKhoanInfo.isPresent() || !taiKhoanService.isMatKhauHopLe(loginRequest.getMatKhau(), taiKhoanInfo.get().getMatKhau())) {
            return new ResponseEntity<>(new MessageResponse("Sai_mat_khau"), HttpStatus.BAD_REQUEST);
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getTaiKhoan(), loginRequest.getMatKhau()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
        return ResponseEntity.ok(new JwtResponse(jwt, refreshToken.getToken()));
    }


    @PostMapping("/cap-lai-token")
    public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getTaiKhoan)
                .map(user -> {
                    Authentication authentication = jwtUtils.getAuthenticationFromUser(user);
                    String token = jwtUtils.generateJwtToken(authentication);
                    return ResponseEntity.ok(new JwtResponse(token, requestRefreshToken));
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                        "Refresh token is not in database!"));
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
        try {
            if (request.getQuyen() == TaiKhoan.Quyen.SinhVien) {
                taiKhoanService.themMoiSinhVien(request.getTenDangNhap(), request.getMatKhau(),
                        request.getEmail(), request.getQuyen(), request.getTenDayDu(),
                        request.getGioiTinh(), request.getNamNhapHoc());
            } else if (request.getQuyen() == TaiKhoan.Quyen.GiangVien) {
                Optional<ChucDanh> chucDanh = chucDanhRepository.findById(request.getMaChucDanh());
                taiKhoanService.themMoiGiangVien(request.getTenDangNhap(), request.getMatKhau(),
                        request.getEmail(), request.getQuyen(), request.getTenDayDu(),
                        request.getGioiTinh(), chucDanh.get());
            } else {
                return new ResponseEntity<>(new MessageResponse("Quyền tài khoản không hợp lệ"), HttpStatus.BAD_REQUEST);
            }

            return ResponseEntity.ok(new MessageResponse("Thêm tài khoản thành công!"));
        } catch (EntityExistsException e) {
            return new ResponseEntity<>(new MessageResponse("Tên đăng nhập đã tồn tại"), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("Lỗi khi thêm tài khoản"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
