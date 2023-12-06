package com.quanly.hoatdongcongdong.controller;

import com.quanly.hoatdongcongdong.payload.request.HuyHoatDongRequest;
import com.quanly.hoatdongcongdong.payload.response.MessageResponse;
import com.quanly.hoatdongcongdong.repository.GiangVienRepository;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/dang-ky-hoat-dong")
@CrossOrigin(value = "*")
public class DangKyHoatDongController {

    @Autowired
    private DangKyHoatDongService dangKyHoatDongService;
    @Autowired
    private DangKyHoatDongRepository dangKyHoatDongRepository;
    @Autowired
    private GiangVienRepository giangVienRepository;
    @Autowired
    private HoatDongRepository hoatDongRepository;
    @Autowired
    private HoatDongService hoatDongService;
    @Autowired
    private TaiKhoanService taiKhoanService;
    @Autowired
    private GiangVienService giangVienService;
    @Autowired
    private ThongBaoService thongBaoService;
    @GetMapping("/hoat-dong-da-dien-ra")
    public ResponseEntity<List<HoatDong>> getHoatDongSapDienRa() {
        List<HoatDong> hoatDongs = dangKyHoatDongRepository.findHoatDongByDangKyChuaDuyetVaDaDienRa();
        return ResponseEntity.ok(hoatDongs);
    }
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
            @RequestParam(required = false) String year,
            @RequestParam(required = false) Long maHoatDong
    ) {
        return dangKyHoatDongService.getDanhSachDangKyHoatDong(page, size,
                sortBy, sortDir, searchTerm, status, startTime, endTime, year, username, maHoatDong);
    }
    //đăng ký hoạt động
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

        // Kiểm tra trạng thái của hoạt động
        if (hoatDong.getTrangThaiHoatDong() != HoatDong.TrangThaiHoatDong.SAP_DIEN_RA) {
            return new ResponseEntity<>(new MessageResponse("expired-register"), HttpStatus.OK);
        }

        Optional<GiangVien> optionalGiangVien = giangVienService.findById(currentUser.getMaTaiKhoan());

        // Kiểm tra xem giảng viên có phải là người tổ chức hoạt động này không
        if (hoatDong.getGiangVienToChucs().contains(optionalGiangVien.get())) {
            return new ResponseEntity<>(new MessageResponse("registration-not-allowed"), HttpStatus.OK);
        }

        // Kiểm tra xem giảng viên đã đăng ký hoạt động này chưa
        if (dangKyHoatDongService.existsByGiangVien_MaTaiKhoanAndHoatDong_MaHoatDong(currentUser.getMaTaiKhoan(), maHoatDong)) {
            return new ResponseEntity<>(new MessageResponse("dangky-exist"), HttpStatus.OK);
        }

        // Tiến hành đăng ký hoạt động
        dangKyHoatDongService.dangKyHoatDong(hoatDong, optionalGiangVien.get());
        return new ResponseEntity<>(new MessageResponse("Đăng ký thành công!"), HttpStatus.OK);
    }


    @PutMapping("/duyet-dang-ky/{maDangKy}")
    public ResponseEntity<?> approveDangKyHoatDong(
            @PathVariable Long maDangKy) {
        Optional<DangKyHoatDong> dangKyHoatDong = dangKyHoatDongService.findById(maDangKy);
        //kiểm tra đăng ký hoạt động có tồn tại hay không
        if (dangKyHoatDong.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("hoatdong-notfound"), HttpStatus.NOT_FOUND);
        }
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
    @PutMapping("/duyet-tat-ca-dang-ky/{maHoatDong}")
    public ResponseEntity<?> approveAllDangKyHoatDongByMaHoatDong(@PathVariable Long maHoatDong) {
        List<DangKyHoatDong> dangKyHoatDongs = dangKyHoatDongService.findAllByMaHoatDong(maHoatDong);

        if (dangKyHoatDongs.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Không tìm thấy đăng ký nào"), HttpStatus.NOT_FOUND);
        }

        for (DangKyHoatDong dangKyHoatDong : dangKyHoatDongs) {
            dangKyHoatDongService.approveDangKyHoatDong(dangKyHoatDong);
            // Tạo thông báo cho từng giảng viên
            String tieuDe = "Duyệt đăng ký hoạt động";
            String nDung = "Yêu cầu đăng ký tham gia hoạt động " +
                    dangKyHoatDong.getHoatDong().getTenHoatDong() + " của bạn đã được duyệt.";
            ThongBao thongBao = thongBaoService.taoMoiThongBao(
                    dangKyHoatDong.getGiangVien().getTaiKhoan(),
                    tieuDe, nDung, ThongBao.TrangThai.ChuaDoc
            );
            thongBaoService.luuThongBao(thongBao);
        }

        return ResponseEntity.ok(new MessageResponse("Đã phê duyệt tất cả các đăng ký cho hoạt động"));
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
    @GetMapping("/hoat-dong/{ten}")
    public ResponseEntity<?> getHoatDongsByGiangVien(@PathVariable String ten) {
        GiangVien giangVien = giangVienRepository.findByTaiKhoan_TenDangNhap(ten);
        if(giangVien == null){
            return new ResponseEntity<>(new MessageResponse("Không tìm thấy giảng viên với tên đăng nhập " +ten), HttpStatus.NOT_FOUND);
        }
        List<HoatDong> hoatDongs = dangKyHoatDongRepository.findHoatDongsByGiangVien(ten);
        return ResponseEntity.ok(hoatDongs);
    }
    @GetMapping("/giang-vien-tham-gia/{maHoatDong}")
    public ResponseEntity<?> getGiangViensByHoatDong(@PathVariable Long maHoatDong) {
        Optional<HoatDong> hoatDong = hoatDongRepository.findById(maHoatDong);
        if(hoatDong == null){
            return new ResponseEntity<>(new MessageResponse("Không tìm thấy hoatdong với mã " +maHoatDong), HttpStatus.NOT_FOUND);
        }
        List<GiangVien> giangVien = dangKyHoatDongRepository.findGiangViensByHoatDong(maHoatDong);
        return ResponseEntity.ok(giangVien);
    }
    //lấy danh sách hoạt động của một giảng viên theo năm
    @GetMapping("/dang-ky")
    public ResponseEntity<List<HoatDong>> getDangKyHoatDongByGiangVienAndYear(
            @RequestParam("maTaiKhoan") Long maTaiKhoan,
            @RequestParam("nam") int nam) {

        List<HoatDong> hoatDongs = dangKyHoatDongService.getHoatDongDangKyByGiangVien(maTaiKhoan, nam);
        return ResponseEntity.ok(hoatDongs);
    }
}
