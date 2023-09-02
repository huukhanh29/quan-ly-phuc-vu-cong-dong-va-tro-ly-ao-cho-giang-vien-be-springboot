package com.quanly.hoatdongcongdong.controller;

import com.quanly.hoatdongcongdong.entity.*;
import com.quanly.hoatdongcongdong.payload.request.MatKhauMoiRequest;
import com.quanly.hoatdongcongdong.payload.response.JwtResponse;
import com.quanly.hoatdongcongdong.sercurity.jwt.JwtUtils;
import com.quanly.hoatdongcongdong.service.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/tai-khoan")
public class TaiKhoanController {
    @Autowired
    private TaiKhoanService taiKhoanService;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private GioTichLuyService gioTichLuyService;
    @Autowired
    private GiangVienService giangVienService;
    @Autowired
    private SinhVienService sinhVienService;
    @Autowired
    private RefreshTokenService refreshTokenService;
    @GetMapping("/thong-tin")
    public ResponseEntity<?> layThongTinNguoiDung(HttpServletRequest httpServletRequest) {
        try {
            TaiKhoan taiKhoan = taiKhoanService.getCurrentUser(httpServletRequest);
            if(taiKhoan.getQuyen() == TaiKhoan.Quyen.SinhVien){
                Optional<SinhVien> sinhVien = sinhVienService.findById(taiKhoan.getMaTaiKhoan());
                return ResponseEntity.ok(sinhVien.get());
            }

            if(taiKhoan.getQuyen() == TaiKhoan.Quyen.GiangVien){
                Optional<GiangVien> giangVien = giangVienService.findById(taiKhoan.getMaTaiKhoan());
                return ResponseEntity.ok(giangVien.get());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy người dùng");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy người dùng");
        }
    }
    @GetMapping("/lay-danh-sach")
    public ResponseEntity<?> getAllUsersByRole(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "maTaiKhoan") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir,
            @RequestParam(required = false, defaultValue = "") String searchTerm,
            @RequestParam(required = false, defaultValue = "") String userRole
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable paging = PageRequest.of(page, size, sort);

        Specification<?> spec = null;

        if (!searchTerm.isEmpty()) {
            spec = (root, criteriaQuery, criteriaBuilder) -> {
                String pattern = "%" + searchTerm + "%";
                return criteriaBuilder.or(
                        criteriaBuilder.like(root.get("taiKhoan").get("tenDayDu"), pattern),
                        criteriaBuilder.like(root.get("taiKhoan").get("email"), pattern),
                        criteriaBuilder.like(root.get("taiKhoan").get("tenDangNhap"), pattern)
                );
            };
        }

        Page<?> result;
        if (userRole.equals("SinhVien")) {
            result = sinhVienService.findAllWithSpec((Specification<SinhVien>) spec, paging);
        } else if (userRole.equals("GiangVien")) {
            result = giangVienService.findAllWithSpec((Specification<GiangVien>) spec, paging);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Không có quyền truy cập danh sách này.");
        }

        return ResponseEntity.ok(result);
    }


    @PutMapping("/cap-nhat-thong-tin")
    public ResponseEntity<?> updateUserProfile(@RequestBody TaiKhoan request,
                                               HttpServletRequest httpServletRequest) {
        try {
            TaiKhoan currentUser = taiKhoanService.getCurrentUser(httpServletRequest);
            taiKhoanService.capNhatThongTinNguoiDung(currentUser.getMaTaiKhoan(),
                    request.getSoDienThoai(),
                    request.getNgaySinh(),
                    request.getGioiTinh(),
                    request.getDiaChi());
            return ResponseEntity.ok(currentUser);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy người dùng");
        }
    }
    @PutMapping("/doi-mat-khau")
    public ResponseEntity<?> doiMatKhau(@Valid @RequestBody MatKhauMoiRequest matKhauMoiRequest,
                                        HttpServletRequest httpServletRequest) {
        try {
            TaiKhoan currentUser = taiKhoanService.getCurrentUser(httpServletRequest);

            if (!taiKhoanService.isMatKhauHopLe(matKhauMoiRequest.getMatKhauCu(), currentUser.getMatKhau())) {
                return ResponseEntity.badRequest().body("NOMATCH");
            }
            if (taiKhoanService.isMatKhauHopLe(matKhauMoiRequest.getMatKhauMoi(), currentUser.getMatKhau())) {
                return ResponseEntity.badRequest().body("NOCHANGE");
            }
            taiKhoanService.capNhatMatKhauNguoiDung(currentUser.getMaTaiKhoan(), matKhauMoiRequest.getMatKhauMoi());

            // Cấp lại token và gửi trong response
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String jwt = jwtUtils.generateJwtToken(authentication);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

            JwtResponse response = new JwtResponse(jwt, refreshToken.getToken(), userDetails.getUsername(), userDetails.getAuthority().getAuthority());
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy người dùng");
        }
    }
    //lấy danh sách năm đã đăng ký hoạt động của mot giang vien
    @GetMapping("/danh-sach-nam-dang-ky-hoat-dong")
    public ResponseEntity<?> getAcademicYearsByUser(HttpServletRequest httpServletRequest) {
        Long maGv = taiKhoanService.getCurrentUser(httpServletRequest).getMaTaiKhoan();
        List<String> academicYears = gioTichLuyService.findDistinctNamHocByGiangVien(maGv);
        return ResponseEntity.ok(academicYears);
    }

    @GetMapping("/ds-giang-vien-khen-thuong-hoac-khien-trach")
    public List<Map<String, Object>> getGiangVien(HttpServletRequest httpServletRequest,
            @RequestParam(required = false) String namHoc,
                                                  @RequestParam(defaultValue = "Khen thưởng") String loai) {
        Long maGv = taiKhoanService.getCurrentUser(httpServletRequest).getMaTaiKhoan();
        List<String> academicYears = gioTichLuyService.findDistinctNamHocByGiangVien(maGv);
        if (namHoc == null || namHoc.isEmpty()) {
            if (!academicYears.isEmpty()) {
                namHoc = academicYears.get(0);
            }else{
                return null;
            }
        }
        List<GioTichLuy> danhSachGioTichLuyCuaGiangVien = gioTichLuyService.findByNamHoc(namHoc);
        List<GiangVien> danhSachGiangVien = giangVienService.findAll();
        List<GiangVien> giangVienKetQua = new ArrayList<>();
        if (loai.equals("Khen thưởng")) {
            giangVienKetQua = danhSachGioTichLuyCuaGiangVien.stream()
                    .filter(gioTichLuy -> gioTichLuy.getTongSoGio() >= gioTichLuy.getGiangVien().getChucDanh().getGioBatBuoc())
                    .map(GioTichLuy::getGiangVien)
                    .collect(Collectors.toList());
        } else if (loai.equals("Khiển trách")) {
            giangVienKetQua = danhSachGiangVien.stream()
                    .filter(giangVien -> {
                        GioTichLuy gioTichLuyCuaGiangVien = danhSachGioTichLuyCuaGiangVien.stream()
                                .filter(gioTichLuy -> gioTichLuy.getGiangVien().getMaTaiKhoan().equals(giangVien.getMaTaiKhoan()))
                                .findFirst()
                                .orElse(null);
                        return gioTichLuyCuaGiangVien == null || gioTichLuyCuaGiangVien.getTongSoGio() < gioTichLuyCuaGiangVien.getGiangVien().getChucDanh().getGioBatBuoc();
                    })
                    .collect(Collectors.toList());
        }
        List<Map<String, Object>> ketQua = new ArrayList<>();
        for (GiangVien giangVien : giangVienKetQua) {
            Map<String, Object> thongTinGiangVien = new HashMap<>();
            int tongSoGio = 0;
            GioTichLuy gioTichLuyCuaGiangVien = gioTichLuyService.findByGiangVien_MaTaiKhoanAndNamHoc(giangVien.getMaTaiKhoan(),namHoc);
            if (gioTichLuyCuaGiangVien != null) {
                tongSoGio = gioTichLuyCuaGiangVien.getTongSoGio();
            }
            thongTinGiangVien.put("hoTen", giangVien.getTaiKhoan().getTenDayDu());
            thongTinGiangVien.put("tenDangNhap", giangVien.getTaiKhoan().getTenDangNhap());
            thongTinGiangVien.put("email", giangVien.getTaiKhoan().getEmail());
            thongTinGiangVien.put("chucDanh", giangVien.getChucDanh().getTenChucDanh());
            thongTinGiangVien.put("tongSoGio", tongSoGio);
            thongTinGiangVien.put("soGioBatBuoc", giangVien.getChucDanh().getGioBatBuoc());
            ketQua.add(thongTinGiangVien);
        }
        return ketQua;
    }
}
