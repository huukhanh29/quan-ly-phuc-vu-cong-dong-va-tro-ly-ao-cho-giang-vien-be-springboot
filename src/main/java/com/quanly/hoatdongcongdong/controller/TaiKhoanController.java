package com.quanly.hoatdongcongdong.controller;

import com.quanly.hoatdongcongdong.entity.*;
import com.quanly.hoatdongcongdong.payload.request.MatKhauMoiRequest;
import com.quanly.hoatdongcongdong.payload.request.TaiKhoanMoiRequest;
import com.quanly.hoatdongcongdong.payload.request.TokenRefreshRequest;
import com.quanly.hoatdongcongdong.payload.request.TrangThaiTaiKhoanRequest;
import com.quanly.hoatdongcongdong.payload.response.JwtResponse;
import com.quanly.hoatdongcongdong.payload.response.MessageResponse;
import com.quanly.hoatdongcongdong.repository.GioTichLuyRepository;
import com.quanly.hoatdongcongdong.repository.KhoaRepository;
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
    private GioTichLuyRepository gioTichLuyRepository;
    @Autowired
    private GiangVienService giangVienService;
    @Autowired
    private SinhVienService sinhVienService;
    @Autowired
    private RefreshTokenService refreshTokenService;
    @Autowired
    private ChucDanhService chucDanhService;
    @Autowired
    private KhoaRepository khoaRepository;

    @GetMapping("/thong-tin")
    public ResponseEntity<?> getUserDetails(
            @RequestParam(value = "tenDangNhap", required = false) String tenDangNhap,
            HttpServletRequest httpServletRequest) {

        TaiKhoan queriedUser;

        if (tenDangNhap != null && !tenDangNhap.isEmpty()) {
            // Tìm kiếm tài khoản theo tên đăng nhập
            queriedUser = taiKhoanService.findByTenDangNhap(tenDangNhap)
                    .orElseThrow(() -> new EntityNotFoundException("not-found"));
        } else {
            // Sử dụng thông tin của người dùng hiện tại
            queriedUser = taiKhoanService.getCurrentUser(httpServletRequest);
            if (queriedUser == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
            }
        }

        switch (queriedUser.getQuyen()) {
            case GiangVien -> {
                GiangVien giaoVien = giangVienService.findById(queriedUser.getMaTaiKhoan())
                        .orElseThrow(() -> new EntityNotFoundException("GiaoVien not found"));
                return ResponseEntity.ok(giaoVien);
            }
            case SinhVien -> {
                SinhVien hocVien = sinhVienService.findById(queriedUser.getMaTaiKhoan())
                        .orElseThrow(() -> new EntityNotFoundException("HocVien not found"));
                return ResponseEntity.ok(hocVien);
            }
            case QuanTriVien -> {
                TaiKhoan quantri = taiKhoanService.findById(queriedUser.getMaTaiKhoan())
                        .orElseThrow(() -> new EntityNotFoundException("Admin not found"));
                return ResponseEntity.ok(quantri);
            }
            default -> {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Quyền không hợp lệ");
            }
        }
    }
    @GetMapping("/thong-tin/{maGv}")
    public ResponseEntity<?> getGv(
            @PathVariable Long maGv) {

        Optional<TaiKhoan> queriedUserOptinal= taiKhoanService.findById(maGv);
        if (queriedUserOptinal.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("gv-notfound"), HttpStatus.NOT_FOUND);
        }
        TaiKhoan queriedUser = queriedUserOptinal.get();

        switch (queriedUser.getQuyen()) {
            case GiangVien -> {
                GiangVien giaoVien = giangVienService.findById(queriedUser.getMaTaiKhoan())
                        .orElseThrow(() -> new EntityNotFoundException("GiaoVien not found"));
                return ResponseEntity.ok(giaoVien);
            }
            case SinhVien -> {
                SinhVien hocVien = sinhVienService.findById(queriedUser.getMaTaiKhoan())
                        .orElseThrow(() -> new EntityNotFoundException("HocVien not found"));
                return ResponseEntity.ok(hocVien);
            }
            case QuanTriVien -> {
                TaiKhoan quantri = taiKhoanService.findById(queriedUser.getMaTaiKhoan())
                        .orElseThrow(() -> new EntityNotFoundException("Admin not found"));
                return ResponseEntity.ok(quantri);
            }
            default -> {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Quyền không hợp lệ");
            }
        }
    }
    @GetMapping("/tat-ca-giang-vien")
    public ResponseEntity<List<GiangVien>> getAllGiangVien() {
        List<GiangVien> giangVienList = giangVienService.getAllGiangVien();
        return ResponseEntity.ok(giangVienList);
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

    @PostMapping("/them-tai-khoan")
    public ResponseEntity<?> themNguoiDung(@Valid @RequestBody TaiKhoanMoiRequest request) {
        if (request.getQuyen() == TaiKhoan.Quyen.SinhVien) {
            taiKhoanService.themMoiSinhVien(request.getTenDangNhap(), request.getMatKhau(),
                    request.getEmail(), request.getQuyen(), request.getTenDayDu(),
                    request.getGioiTinh(), request.getNamNhapHoc(),
                    request.getSoDienThoai(), request.getNgaySinh(), request.getDiaChi());
        } else if (request.getQuyen() == TaiKhoan.Quyen.GiangVien) {
            Optional<ChucDanh> chucDanh = chucDanhService.findById(request.getMaChucDanh());
            if (chucDanh.isEmpty()) {
                return new ResponseEntity<>(new MessageResponse("chucdanh-notfound"), HttpStatus.NOT_FOUND);
            }
            Optional<Khoa> khoa = khoaRepository.findById(request.getMaKhoa());
            if (khoa.isEmpty()) {
                return new ResponseEntity<>(new MessageResponse("khoa-notfound"), HttpStatus.NOT_FOUND);
            }
            taiKhoanService.themMoiGiangVien(request.getTenDangNhap(), request.getMatKhau(),
                    request.getEmail(), request.getQuyen(), request.getTenDayDu(),
                    request.getGioiTinh(), chucDanh.get(), khoa.get(), request.getSoDienThoai(),
                    request.getNgaySinh(), request.getDiaChi());
        } else {
            throw new EntityNotFoundException("Quyền tài khoản không hợp lệ");
        }
        return ResponseEntity.ok(new MessageResponse("Thêm tài khoản thành công!"));
    }

    @PutMapping("/cap-nhat-trang-thai")
    public ResponseEntity<?> updateStatus(
            @RequestBody TrangThaiTaiKhoanRequest trangThaiTaiKhoanRequest,
            HttpServletRequest httpServletRequest) {

        TaiKhoan currentUser = taiKhoanService.getCurrentUser(httpServletRequest);
        Optional<TaiKhoan> taiKhoan = taiKhoanService.findByTenDangNhap(trangThaiTaiKhoanRequest.getTenDangNhap());
        if (currentUser == null) {
            return new ResponseEntity<>(new MessageResponse("NOT_FOUND"), HttpStatus.NOT_FOUND);
        }
        if (currentUser.getQuyen().equals(TaiKhoan.Quyen.QuanTriVien) && taiKhoan.isPresent()) {
            if (taiKhoan.get().getTrangThai().equals(trangThaiTaiKhoanRequest.getTrangThai())) {
                return new ResponseEntity<>(new MessageResponse("NO_CHANGE"), HttpStatus.OK);
            }
            taiKhoan.get().setTrangThai(trangThaiTaiKhoanRequest.getTrangThai());
            taiKhoanService.save(taiKhoan.get());
        } else {
            return new ResponseEntity<>(new MessageResponse("ERROR"), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new MessageResponse("OK"), HttpStatus.OK);
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
            return new ResponseEntity<>(new MessageResponse("taikhoan-notfound"), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/doi-mat-khau")
    public ResponseEntity<?> doiMatKhau(@Valid @RequestBody MatKhauMoiRequest matKhauMoiRequest,
                                        HttpServletRequest httpServletRequest) {
        try {
            TaiKhoan currentUser = taiKhoanService.getCurrentUser(httpServletRequest);

            if (!taiKhoanService.isMatKhauHopLe(matKhauMoiRequest.getMatKhauCu(), currentUser.getMatKhau())) {
                return new ResponseEntity<>(new MessageResponse("not-match"), HttpStatus.OK);
            }
            if (taiKhoanService.isMatKhauHopLe(matKhauMoiRequest.getMatKhauMoi(), currentUser.getMatKhau())) {
                return new ResponseEntity<>(new MessageResponse("no-change"), HttpStatus.OK);
            }
            taiKhoanService.capNhatMatKhauNguoiDung(currentUser.getMaTaiKhoan(), matKhauMoiRequest.getMatKhauMoi());

            // Cấp lại token và gửi trong response
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String jwt = jwtUtils.generateJwtToken(authentication);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
            Long expirationDate = jwtUtils.getExpirationDateFromJwtToken(jwt).getTime() / 1000;
            JwtResponse response = new JwtResponse(jwt, refreshToken.getRefreshtoken(), userDetails.getUsername(), userDetails.getAuthority().getAuthority(), expirationDate);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy người dùng");
        }
    }

    //lấy danh sách năm đã đăng ký hoạt động của mot giang vien
    @GetMapping("/danh-sach-nam-dang-ky-hoat-dong")
    public ResponseEntity<?> getAcademicYearsByUser(HttpServletRequest httpServletRequest) {
        Long maGv = taiKhoanService.getCurrentUser(httpServletRequest).getMaTaiKhoan();
        List<String> academicYears = gioTichLuyService.findDistinctNamByGiangVien(maGv);
        return ResponseEntity.ok(academicYears);
    }

    @PostMapping("/dang-xuat")
    public ResponseEntity<?> logoutUser(@Valid @RequestBody TokenRefreshRequest request,
                                        HttpServletRequest httpServletRequest) {
        refreshTokenService.deleteByRf(request.getRefreshToken());
        return ResponseEntity.ok(new MessageResponse("Log out successful!"));
    }

    @GetMapping("/ds-giang-vien-khen-thuong-hoac-khien-trach")
    public List<Map<String, Object>> getGiangVienKhenThuongHoacKienTrach(
            @RequestParam(required = false) String nam,
            @RequestParam(defaultValue = "Chưa hoàn thành") String loai,
            @RequestParam(required = false) Long khoa
    ) {
        List<GioTichLuy> danhSachGioTichLuyCuaGiangVien = gioTichLuyService.findByNam(nam);
        List<GiangVien> danhSachGiangVien = giangVienService.findAll();

        // Bộ lọc giảng viên theo khoa và trường
        if (khoa != null) {
            danhSachGiangVien = danhSachGiangVien.stream()
                    .filter(giangVien -> giangVien.getKhoa().getMaKhoa().equals(khoa))
                    .collect(Collectors.toList());
        }

        List<GiangVien> giangVienKetQua = new ArrayList<>();
        if (loai.equals("Hoàn thành")) {
            giangVienKetQua = danhSachGioTichLuyCuaGiangVien.stream()
                    .filter(gioTichLuy -> gioTichLuy.getTongSoGio() >= gioTichLuy.getGiangVien().getChucDanh().getGioBatBuoc())
                    .map(GioTichLuy::getGiangVien)
                    .collect(Collectors.toList());
        } else if (loai.equals("Chưa hoàn thành")) {
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
            GioTichLuy gioTichLuyCuaGiangVien =
                    gioTichLuyService.findByGiangVien_MaTaiKhoanAndNam(giangVien.getMaTaiKhoan(), nam);
            if (gioTichLuyCuaGiangVien != null) {
                tongSoGio = gioTichLuyCuaGiangVien.getTongSoGio();
            }
            thongTinGiangVien.put("hoTen", giangVien.getTaiKhoan().getTenDayDu());
            thongTinGiangVien.put("giangVien", giangVien);
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
