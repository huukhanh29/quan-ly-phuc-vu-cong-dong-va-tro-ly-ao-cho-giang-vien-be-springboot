package com.quanly.hoatdongcongdong.controller;

import com.quanly.hoatdongcongdong.payload.request.HuyHoatDongRequest;
import com.quanly.hoatdongcongdong.repository.GiangVienRepository;
import com.quanly.hoatdongcongdong.repository.GioTichLuyRepository;
import com.quanly.hoatdongcongdong.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.quanly.hoatdongcongdong.entity.HoatDong;
import com.quanly.hoatdongcongdong.repository.DangKyHoatDongRepository;
import com.quanly.hoatdongcongdong.repository.HoatDongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.quanly.hoatdongcongdong.entity.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/dang-ky-hoat-dong")
@CrossOrigin(value = "*")
public class DangKyHoatDongController {

    @Autowired
    private DangKyHoatDongService dangKyHoatDongService;
    @Autowired
    private HoatDongService hoatDongService;
    @Autowired
    private TaiKhoanService taiKhoanService;
    @Autowired
    private GiangVienService giangVienService;
    @Autowired
    private ThongBaoService thongBaoService;

    @GetMapping("/lay-danh-sach")
    public Page<DangKyHoatDong> layDanhSachTatCaDangKyHoatDong(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "hoatDong.ngayTao") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir,
            @RequestParam(required = false, defaultValue = "") String searchTerm,
            @RequestParam(required = false, defaultValue = "") String status,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false, defaultValue = "") String year,
            @RequestParam(required = false) Long userId
    ) {
        return dangKyHoatDongService.getDanhSachDangKyHoatDong(page, size, sortBy, sortDir, searchTerm, status, startTime, endTime, year, userId);
    }

    @PostMapping("/{maHoatDong}")
    public ResponseEntity<String> dangKyHoatDong(@PathVariable Long maHoatDong, HttpServletRequest httpServletRequest) {
        // Lấy thông tin người dùng đang đăng nhập
        TaiKhoan currentUser = taiKhoanService.getCurrentUser(httpServletRequest);

        // Tìm hoạt động cần đăng ký
        Optional<HoatDong> optionalHoatDong = hoatDongService.findById(maHoatDong);
        if (optionalHoatDong.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy hoạt động với mã " + maHoatDong);
        }
        HoatDong hoatDong = optionalHoatDong.get();

        if (hoatDong.getTrangThaiHoatDong() != HoatDong.TrangThaiHoatDong.SAP_DIEN_RA) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không thể đăng ký hoạt động do hết thời gian đăng ký");
        }

        Optional<GiangVien> giangVien = giangVienService.findById(currentUser.getMaTaiKhoan());

        if (dangKyHoatDongService.existsByGiangVien_MaTaiKhoanAndHoatDong_MaHoatDong(currentUser.getMaTaiKhoan(), maHoatDong)) {
            return ResponseEntity.badRequest().body("Bạn đã đăng ký hoạt động này trước đó");
        }

        dangKyHoatDongService.dangKyHoatDong(hoatDong, giangVien.get());

        return ResponseEntity.ok("Đăng ký hoạt động thành công");
    }

    @PutMapping("/duyet-dang-ky/{maDangKy}")
    public ResponseEntity<String> approveDangKyHoatDong(
            @PathVariable Long maDangKy) {
        Optional<DangKyHoatDong> dangKyHoatDong = dangKyHoatDongService.findById(maDangKy);

        if (dangKyHoatDong.isEmpty()) {
            return ResponseEntity.badRequest().body("Hoạt động chưa đăng ký");
        }

        if (dangKyHoatDong.get().getTrangThaiDangKy() == DangKyHoatDong.TrangThaiDangKy.Da_Duyet) {
            return ResponseEntity.badRequest().body("Đã duyệt hoạt động này trước đó");
        }
        // Call the corresponding service method
        dangKyHoatDongService.approveDangKyHoatDong(dangKyHoatDong.get());
        // Tạo thông báo cho người dùng về việc phê duyệt đăng ký hoạt động
        String tieuDe = "Duyệt đăng ký hoạt động";
        String nDung = "Yêu cầu đăng ký tham gia hoạt động " +
                dangKyHoatDong.get().getHoatDong().getTenHoatDong() + " của bạn đã được duyệt.";
        ThongBao thongBao = thongBaoService.taoMoiThongBao(
                dangKyHoatDong.get().getGiangVien().getTaiKhoan(),
                tieuDe, nDung, ThongBao.TrangThai.ChuaDoc
        );
        thongBaoService.luuThongBao(thongBao);

        return ResponseEntity.ok("Phê duyệt đăng ký hoạt động thành công.");
    }

    @PutMapping("/huy-hoat-dong/{maDangKy}")
    public ResponseEntity<String> huyDangKyHoatDong(
            @PathVariable Long maDangKy,
            @RequestBody HuyHoatDongRequest huyHoatDongRequest) {

        Optional<DangKyHoatDong> dangKyHoatDong = dangKyHoatDongService.findById(maDangKy);

        if (dangKyHoatDong.isEmpty()) {
            return ResponseEntity.badRequest().body("Hoạt động chưa đăng ký");
        }
        if (dangKyHoatDong.get().getTrangThaiDangKy().equals(DangKyHoatDong.TrangThaiDangKy.Da_Huy)) {
            return ResponseEntity.badRequest().body("Hoạt động đã hủy trước đó");
        }
        if (!huyHoatDongRequest.getLyDoHuy().equals("")) {
            dangKyHoatDongService.huyDangKyHoatDong(dangKyHoatDong.get(), huyHoatDongRequest);
        } else {
            return ResponseEntity.badRequest().body("Lý do hủy không được trống");
        }

        return ResponseEntity.ok("Hủy đăng ký hoạt động thành công.");
    }
}
