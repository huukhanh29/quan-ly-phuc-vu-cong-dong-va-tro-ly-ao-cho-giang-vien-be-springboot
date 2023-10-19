package com.quanly.hoatdongcongdong.controller;

import com.quanly.hoatdongcongdong.payload.request.HuyHoatDongRequest;
import com.quanly.hoatdongcongdong.payload.response.MessageResponse;
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
import java.util.Optional;

@RestController
@RequestMapping("/api/dang-ky-hoat-dong")
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
            @RequestParam(defaultValue = "hoatDong.thoiGianBatDau") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir,
            @RequestParam(required = false, defaultValue = "") String searchTerm,
            @RequestParam(required = false, defaultValue = "") String status,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String year
    ) {
        return dangKyHoatDongService.getDanhSachDangKyHoatDong(page, size,
                sortBy, sortDir, searchTerm, status, startTime, endTime, year, username);
    }

    @PostMapping("/{maHoatDong}")
    public ResponseEntity<?> dangKyHoatDong(@PathVariable Long maHoatDong, HttpServletRequest httpServletRequest) {
        // Lấy thông tin người dùng đang đăng nhập
        TaiKhoan currentUser = taiKhoanService.getCurrentUser(httpServletRequest);

        // Tìm hoạt động cần đăng ký
        Optional<HoatDong> optionalHoatDong = hoatDongService.findById(maHoatDong);
        if (optionalHoatDong.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("hoatdong-notfound"), HttpStatus.NOT_FOUND);
        }
        HoatDong hoatDong = optionalHoatDong.get();

        if (hoatDong.getTrangThaiHoatDong() != HoatDong.TrangThaiHoatDong.SAP_DIEN_RA) {
            return new ResponseEntity<>(new MessageResponse("expired-register"), HttpStatus.OK);
        }

        Optional<GiangVien> giangVien = giangVienService.findById(currentUser.getMaTaiKhoan());

        if (dangKyHoatDongService.existsByGiangVien_MaTaiKhoanAndHoatDong_MaHoatDong(currentUser.getMaTaiKhoan(), maHoatDong)) {
            return new ResponseEntity<>(new MessageResponse("dangky-exist"), HttpStatus.OK);
        }

        dangKyHoatDongService.dangKyHoatDong(hoatDong, giangVien.get());

        return new ResponseEntity<>(new MessageResponse("Đăng ký thành công!"), HttpStatus.OK);
    }

    @PutMapping("/duyet-dang-ky/{maDangKy}")
    public ResponseEntity<?> approveDangKyHoatDong(
            @PathVariable Long maDangKy) {
        Optional<DangKyHoatDong> dangKyHoatDong = dangKyHoatDongService.findById(maDangKy);

        if (dangKyHoatDong.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("hoatdong-notfound"), HttpStatus.NOT_FOUND);
        }

        if (dangKyHoatDong.get().getTrangThaiDangKy() == DangKyHoatDong.TrangThaiDangKy.Da_Duyet) {
            return new ResponseEntity<>(new MessageResponse("hoatdong-exist"), HttpStatus.OK);
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

        return ResponseEntity.ok(new MessageResponse("đã phê duyệt"));
    }

    @PutMapping("/huy-hoat-dong/{maDangKy}")
    public ResponseEntity<?> huyDangKyHoatDong(
            @PathVariable Long maDangKy,
            @RequestBody HuyHoatDongRequest huyHoatDongRequest) {

        Optional<DangKyHoatDong> dangKyHoatDong = dangKyHoatDongService.findById(maDangKy);

        if (dangKyHoatDong.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("hoatdong-notfound"), HttpStatus.NOT_FOUND);
        }
        if (dangKyHoatDong.get().getTrangThaiDangKy().equals(DangKyHoatDong.TrangThaiDangKy.Da_Huy)) {
            return new ResponseEntity<>(new MessageResponse("hoatdong-exist"), HttpStatus.OK);
        }
        if (!huyHoatDongRequest.getLyDoHuy().equals("")) {
            dangKyHoatDongService.huyDangKyHoatDong(dangKyHoatDong.get(), huyHoatDongRequest);
            String tieuDe = "Hủy đăng ký hoạt động";
            String nDung = "Xác nhận tham gia hoạt động " +
                    dangKyHoatDong.get().getHoatDong().getTenHoatDong() + " của bạn đã bị hủy.";
            ThongBao thongBao = thongBaoService.taoMoiThongBao(
                    dangKyHoatDong.get().getGiangVien().getTaiKhoan(),
                    tieuDe, nDung, ThongBao.TrangThai.ChuaDoc
            );
            thongBaoService.luuThongBao(thongBao);
        } else {
            return new ResponseEntity<>(new MessageResponse("lydo-notempty"), HttpStatus.OK);
        }
        return ResponseEntity.ok(new MessageResponse("đã hủy"));
    }
    @PostMapping("/kiem-tra/{maHoatDong}")
    public ResponseEntity<?> kiemTraDangKyKhoaHoc(@PathVariable Long maHoatDong, HttpServletRequest httpServletRequest) {
        TaiKhoan currentUser = taiKhoanService.getCurrentUser(httpServletRequest);

        // Tìm hoạt động cần đăng ký
        Optional<HoatDong> optionalHoatDong = hoatDongService.findById(maHoatDong);
        if (optionalHoatDong.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("hoatdong-notfound"), HttpStatus.NOT_FOUND);
        }
        HoatDong hoatDong = optionalHoatDong.get();
        boolean check = dangKyHoatDongService.kiemTraDangKyHoatDong(currentUser.getTenDangNhap(), hoatDong.getMaHoatDong());
        if(check){
            DangKyHoatDong dangKyHoatDong = dangKyHoatDongService.findByGiangVienAndHoatDong(currentUser.getTenDangNhap(), hoatDong.getMaHoatDong());
            if(dangKyHoatDong != null){
                if(dangKyHoatDong.getTrangThaiDangKy() == DangKyHoatDong.TrangThaiDangKy.Da_Duyet){
                    return new ResponseEntity<>(new MessageResponse("daduyet"), HttpStatus.OK);
                }else if(dangKyHoatDong.getTrangThaiDangKy() == DangKyHoatDong.TrangThaiDangKy.Da_Huy){
                    return new ResponseEntity<>(new MessageResponse("dahuy"), HttpStatus.OK);
                }else if(dangKyHoatDong.getTrangThaiDangKy() == DangKyHoatDong.TrangThaiDangKy.Chua_Duyet){
                    return new ResponseEntity<>(new MessageResponse("dadangky"), HttpStatus.OK);
                }
            }
        }else {
            return new ResponseEntity<>(new MessageResponse("false"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new MessageResponse("unknown"), HttpStatus.OK);
    }
}
