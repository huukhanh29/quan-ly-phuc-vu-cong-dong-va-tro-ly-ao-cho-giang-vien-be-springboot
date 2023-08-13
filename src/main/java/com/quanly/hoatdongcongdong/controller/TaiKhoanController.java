package com.quanly.hoatdongcongdong.controller;

import com.quanly.hoatdongcongdong.entity.GiangVien;
import com.quanly.hoatdongcongdong.entity.GioTichLuy;
import com.quanly.hoatdongcongdong.entity.TaiKhoan;
import com.quanly.hoatdongcongdong.payload.request.MatKhauMoiRequest;
import com.quanly.hoatdongcongdong.payload.response.JwtResponse;
import com.quanly.hoatdongcongdong.payload.response.ThongTinTaiKhoanResponse;
import com.quanly.hoatdongcongdong.repository.GiangVienRepository;
import com.quanly.hoatdongcongdong.repository.GioTichLuyRepository;
import com.quanly.hoatdongcongdong.sercurity.jwt.JwtUtils;
import com.quanly.hoatdongcongdong.sercurity.services.TaiKhoanService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("/tai-khoan")
public class TaiKhoanController {
    @Autowired
    private TaiKhoanService taiKhoanService;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private GioTichLuyRepository gioTichLuyRepository;
    @Autowired
    private GiangVienRepository giangVienRepository;
    @GetMapping("/thong-tin")
    public ResponseEntity<?> layThongTinNguoiDung(HttpServletRequest httpServletRequest) {
        try {
            Long maTaiKhoan = taiKhoanService.getCurrentUser(httpServletRequest).getMaTaiKhoan();
            ThongTinTaiKhoanResponse nguoiDungDTO = taiKhoanService.layThongTinNguoiDung(maTaiKhoan);
            return ResponseEntity.ok(nguoiDungDTO);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy người dùng");
        }
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
            ThongTinTaiKhoanResponse nguoiDungDTO = taiKhoanService.layThongTinNguoiDung(currentUser.getMaTaiKhoan());
            return ResponseEntity.ok(nguoiDungDTO);
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

            taiKhoanService.capNhatMatKhauNguoiDung(currentUser.getMaTaiKhoan(), matKhauMoiRequest.getMatKhauMoi());

            // Cấp lại token và gửi trong response
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String jwt = jwtUtils.generateJwtToken(authentication);

            return ResponseEntity.ok(new JwtResponse(jwt));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy người dùng");
        }
    }
    //lấy danh sách năm đã đăng ký hoạt động của mot giang vien
    @GetMapping("/danh-sach-nam-dang-ky-hoat-dong")
    public ResponseEntity<?> getAcademicYearsByUser(HttpServletRequest httpServletRequest) {
        Long maGv = taiKhoanService.getCurrentUser(httpServletRequest).getMaTaiKhoan();
        List<String> academicYears = gioTichLuyRepository.findDistinctNamHocByGiangVien(maGv);
        return ResponseEntity.ok(academicYears);
    }

    @GetMapping("/ds-giang-vien-khen-thuong-hoac-khien-trach")
    public List<Map<String, Object>> getGiangVien(HttpServletRequest httpServletRequest,
            @RequestParam(required = false) String namHoc,
                                                  @RequestParam(defaultValue = "Khen thưởng") String loai) {
        Long maGv = taiKhoanService.getCurrentUser(httpServletRequest).getMaTaiKhoan();
        List<String> academicYears = gioTichLuyRepository.findDistinctNamHocByGiangVien(maGv);
        if (namHoc == null || namHoc.isEmpty()) {
            if (!academicYears.isEmpty()) {
                namHoc = academicYears.get(0);
            }else{
                return null;
            }
        }
        List<GioTichLuy> danhSachGioTichLuyCuaGiangVien = gioTichLuyRepository.findByNamHoc(namHoc);
        List<GiangVien> danhSachGiangVien = giangVienRepository.findAll();
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
            GioTichLuy gioTichLuyCuaGiangVien = gioTichLuyRepository.findByGiangVien_MaTaiKhoanAndNamHoc(giangVien.getMaTaiKhoan(),namHoc);
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
