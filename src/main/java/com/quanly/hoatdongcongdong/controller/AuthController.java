package com.quanly.hoatdongcongdong.controller;
import com.quanly.hoatdongcongdong.entity.TaiKhoan;
import com.quanly.hoatdongcongdong.entity.RefreshToken;
import com.quanly.hoatdongcongdong.exception.TokenRefreshException;
import com.quanly.hoatdongcongdong.payload.request.LoginRequest;
import com.quanly.hoatdongcongdong.payload.request.SignupRequest;
import com.quanly.hoatdongcongdong.payload.request.TokenRefreshRequest;
import com.quanly.hoatdongcongdong.payload.response.JwtResponse;
import com.quanly.hoatdongcongdong.payload.response.MessageResponse;
import com.quanly.hoatdongcongdong.payload.response.TokenRefreshResponse;
import com.quanly.hoatdongcongdong.repository.TaiKhoanRepository;
import com.quanly.hoatdongcongdong.sercurity.jwt.JwtUtils;
import com.quanly.hoatdongcongdong.sercurity.services.RefreshTokenService;
import com.quanly.hoatdongcongdong.sercurity.services.TaiKhoanService;
import com.quanly.hoatdongcongdong.sercurity.services.UserDetailsImpl;
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

  @PostMapping("/signout")
  public ResponseEntity<?> logoutUser() {
    UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Long userId = userDetails.getId();
    refreshTokenService.deleteByMaTaiKhoan(userId);
    return ResponseEntity.ok(new MessageResponse("Log out successful!"));
  }
//  @PostMapping("/tao-moi")
//  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
//    if (taiKhoanRepository.existsByTenTaiKhoan(signUpRequest.getTaiKhoan())) {
//      return ResponseEntity
//          .badRequest()
//          .body(new MessageResponse("Ten_tai_khoan_da_ton_tai"));
//    }
//
//    if (taiKhoanRepository.existsByEmail(signUpRequest.getEmail())) {
//      return ResponseEntity
//          .badRequest()
//          .body(new MessageResponse("Email_da_ton_tai"));
//    }
//    if(signUpRequest.getQuyen().equals("GiangVien")){
//      TaiKhoan user = new TaiKhoan(signUpRequest.getTen(), signUpRequest.getTaiKhoan(),
//              encoder.encode(signUpRequest.getMatKhau()),signUpRequest.getEmail(), TaiKhoan.Quyen.GiangVien);
//      taiKhoanRepository.save(user);
//    }else {
//      TaiKhoan user = new TaiKhoan(signUpRequest.getTen(), signUpRequest.getTaiKhoan(),
//              encoder.encode(signUpRequest.getMatKhau()), signUpRequest.getEmail(), Quyen.BENHNHAN);
//      taiKhoanRepository.save(user);
//    }
//
//    return ResponseEntity.ok(new MessageResponse("Dang_ky_thanh_cong"));
//  }
}
