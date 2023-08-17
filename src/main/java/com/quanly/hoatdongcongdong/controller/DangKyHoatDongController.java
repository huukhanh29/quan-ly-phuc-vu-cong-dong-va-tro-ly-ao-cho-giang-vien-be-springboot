package com.quanly.hoatdongcongdong.controller;

import com.quanly.hoatdongcongdong.payload.request.HuyHoatDongRequest;
import com.quanly.hoatdongcongdong.repository.GiangVienRepository;
import com.quanly.hoatdongcongdong.repository.GioTichLuyRepository;
import com.quanly.hoatdongcongdong.sercurity.services.TaiKhoanService;
import com.quanly.hoatdongcongdong.sercurity.services.ThongBaoService;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dang-ky-hoat-dong")
@CrossOrigin(value = "*")
public class DangKyHoatDongController {
    @Autowired
    private HoatDongRepository hoatDongRepository;
    @Autowired
    private DangKyHoatDongRepository dangKyHoatDongRepository;
    @Autowired
    private GiangVienRepository giangVienRepository;
    @Autowired
    private TaiKhoanService taiKhoanService;
    @Autowired
    private ThongBaoService thongBaoService;
    @Autowired
    private GioTichLuyRepository gioTichLuyRepository;

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
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable paging = PageRequest.of(page, size, sort);

        Specification<DangKyHoatDong> spec = Specification.where(null);
        if (!searchTerm.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                String pattern = "%" + searchTerm + "%";
                return criteriaBuilder.or(
                        criteriaBuilder.like(root.get("giangVien").get("taiKhoan").get("tenDayDu"), pattern),
                        criteriaBuilder.like(root.get("hoatDong").get("tenHoatDong"), pattern)
                );
            });
        }
        if (!status.isEmpty()) {
            spec = spec.and((root, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("trangThaiDangKy"), DangKyHoatDong.TrangThaiDangKy.valueOf(status)));
        }
        if (startTime != null) {
            LocalDateTime startTimes = LocalDate.parse(startTime).atStartOfDay();
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("hoatDong").get("thoiGianBatDau"), startTimes));
        }
        if (endTime != null) {
            LocalDate date = LocalDate.parse(endTime);
            LocalDateTime endTimes = date.atTime(23, 59, 59);
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("hoatDong").get("thoiGianKetThuc"), endTimes));
        }
        if (!year.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                LocalDateTime startOfYear = LocalDateTime.of(Integer.parseInt(year), 1, 1, 0, 0, 0);
                LocalDateTime endOfYear = LocalDateTime.of(Integer.parseInt(year), 12, 31, 23, 59, 59);
                return criteriaBuilder.between(root.get("hoatDong").get("thoiGianBatDau"), startOfYear, endOfYear);
            });
        }
        if (userId != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("giangVien").get("maTaiKhoan"), userId));
        }
        return dangKyHoatDongRepository.findAll(spec, paging);
    }

    //=====chưa hoàn thành====
    //đăng ký hoạt động
    @PostMapping("/{maHoatDong}")
    public ResponseEntity<String> dangKyHoatDong(@PathVariable Long maHoatDong, HttpServletRequest httpServletRequest) {
        // Lấy thông tin người dùng đang đăng nhập
        TaiKhoan currentUser = taiKhoanService.getCurrentUser(httpServletRequest);

        // Tìm hoạt động cần đăng ký
        Optional<HoatDong> optionalHoatDong = hoatDongRepository.findById(maHoatDong);
        if (optionalHoatDong.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy hoạt động với mã " + maHoatDong);
        }
        if (optionalHoatDong.get().getTrangThaiHoatDong() != HoatDong.TrangThaiHoatDong.SAP_DIEN_RA) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không thể đăng ký hoạt động do hết thời gian đăng ký");
        }
        HoatDong hoatDong = optionalHoatDong.get();

        // Kiểm tra xem hoạt động có trong danh sách đăng ký của giảng viên hay không
        if (dangKyHoatDongRepository.existsByGiangVien_MaTaiKhoanAndHoatDong_MaHoatDong(currentUser.getMaTaiKhoan(), maHoatDong)) {
            return ResponseEntity.badRequest().body("Bạn đã đăng ký hoạt động này trước đó");
        }
        //Tìm giảng viên
        Optional<GiangVien> giangVien = giangVienRepository.findById(currentUser.getMaTaiKhoan());
        // Tạo đối tượng đăng ký hoạt động và lưu vào cơ sở dữ liệu
        DangKyHoatDong dangKyHoatDong = new DangKyHoatDong();
        dangKyHoatDong.setGiangVien(giangVien.get());
        dangKyHoatDong.setHoatDong(hoatDong);
        dangKyHoatDong.setTrangThaiDangKy(DangKyHoatDong.TrangThaiDangKy.Chua_Duyet);
        dangKyHoatDongRepository.save(dangKyHoatDong);

        return ResponseEntity.ok("Đăng ký hoạt động thành công");
    }

    @PutMapping("/duyet-dang-ky/{maDangKy}")
    public ResponseEntity<String> approveDangKyHoatDong(
            @PathVariable Long maDangKy) {

        // Lấy thông tin đăng ký hoạt động
        DangKyHoatDong dangKyHoatDong = dangKyHoatDongRepository.findById(maDangKy).orElse(null);

        // Nếu không tìm thấy đăng ký hoặc đã bị xóa
        if (dangKyHoatDong == null) {
            return ResponseEntity.badRequest().body("Hoạt động chưa đăng ký");
        }

        if (dangKyHoatDong.getTrangThaiDangKy() == DangKyHoatDong.TrangThaiDangKy.Da_Duyet) {
            return ResponseEntity.badRequest().body("Đã duyệt hoạt động này trước đó");
        }

        // Thực hiện phê duyệt đăng ký hoạt động
        dangKyHoatDong.setTrangThaiDangKy(DangKyHoatDong.TrangThaiDangKy.Da_Duyet);
        dangKyHoatDongRepository.save(dangKyHoatDong);
        //cộng điểm cho giảng viên đăng ký
        GiangVien giangVien = dangKyHoatDong.getGiangVien();
        LocalDateTime thoiGianBatDau = dangKyHoatDong.getHoatDong().getThoiGianBatDau();
        int namBatDau = thoiGianBatDau.getYear();
        int namKetThuc;
        // Nếu thời gian bắt đầu nằm trong tháng 9 trở đi hoặc trong các tháng hè (từ tháng 7 đến tháng 8)
        if (thoiGianBatDau.getMonthValue() >= 7) {
            namKetThuc = namBatDau + 1;
        } else {
            namKetThuc = namBatDau;
            namBatDau = namKetThuc - 1;
        }
        String namHoc = namBatDau + "-" + namKetThuc;
        if (giangVien != null) {
            int gioTichLuyThamGia = dangKyHoatDong.getHoatDong().getGioTichLuyThamGia();
            GioTichLuy gioTichLuy = gioTichLuyRepository.findByGiangVien_MaTaiKhoan(giangVien.getMaTaiKhoan());
            if (gioTichLuy == null) {
                gioTichLuy = new GioTichLuy();
                gioTichLuy.setGiangVien(giangVien);
                gioTichLuy.setTongSoGio(gioTichLuyThamGia);
                gioTichLuy.setNamHoc(namHoc);
                gioTichLuyRepository.save(gioTichLuy);
            } else {
                gioTichLuy.setTongSoGio(gioTichLuy.getTongSoGio() + gioTichLuyThamGia);
                gioTichLuyRepository.save(gioTichLuy);
            }
        }
        //cộng điểm cho giảng viên tổ chức nếu có
        // Lấy danh sách GiangVienToChuc từ hoạt động đã duyệt
        List<GiangVien> giangVienToChucs = dangKyHoatDong.getHoatDong().getGiangVienToChucs();

        // Duyệt qua danh sách GiangVienToChuc và cộng điểm theo gioTichLuyToChuc
        for (GiangVien giangVienToChuc : giangVienToChucs) {
            int gioTichLuyToChuc = dangKyHoatDong.getHoatDong().getGioTichLuyToChuc();
            GioTichLuy gioTichLuyToChucEntity = gioTichLuyRepository.findByGiangVien_MaTaiKhoan(giangVienToChuc.getMaTaiKhoan());

            if (gioTichLuyToChucEntity == null) {
                gioTichLuyToChucEntity = new GioTichLuy();
                gioTichLuyToChucEntity.setGiangVien(giangVienToChuc);
                gioTichLuyToChucEntity.setTongSoGio(gioTichLuyToChuc);
                gioTichLuyToChucEntity.setNamHoc(namHoc);
                gioTichLuyRepository.save(gioTichLuyToChucEntity);
            } else {
                gioTichLuyToChucEntity.setTongSoGio(gioTichLuyToChucEntity.getTongSoGio() + gioTichLuyToChuc);
                gioTichLuyRepository.save(gioTichLuyToChucEntity);
            }
        }

        // Tạo thông báo cho người dùng về việc phê duyệt đăng ký hoạt động
        String tieuDe = "Duyệt đăng ký hoạt động";
        String nDung = "Yêu cầu đăng ký tham gia hoạt động " +
                dangKyHoatDong.getHoatDong().getTenHoatDong() + " của bạn đã được duyệt.";
        ThongBao thongBao = thongBaoService.taoMoiThongBao(
                dangKyHoatDong.getGiangVien().getTaiKhoan(),
                tieuDe, nDung, ThongBao.TrangThai.ChuaDoc
        );
        thongBaoService.luuThongBao(thongBao);

        return ResponseEntity.ok("Phê duyệt đăng ký hoạt động thành công.");
    }

    @PutMapping("/huy-hoat-dong/{maDangKy}")
    public ResponseEntity<String> huyDangKyHoatDong(@PathVariable Long maDangKy,
                                                    @RequestBody HuyHoatDongRequest huyHoatDongRequest) {

        // Lấy thông tin đăng ký hoạt động
        DangKyHoatDong dangKyHoatDong = dangKyHoatDongRepository.findById(maDangKy).orElse(null);

        // Nếu không tìm thấy đăng ký hoặc đã bị xóa
        if (dangKyHoatDong == null) {
            return ResponseEntity.badRequest().body("Hoạt động chưa đăng ký");
        }
        if(dangKyHoatDong.getTrangThaiDangKy().equals(DangKyHoatDong.TrangThaiDangKy.Da_Huy)){
            return ResponseEntity.badRequest().body("Hoạt động đã hủy trước đó");
        }
        if (!huyHoatDongRequest.getLyDoHuy().equals("")) {
            if (dangKyHoatDong.getTrangThaiDangKy() == DangKyHoatDong.TrangThaiDangKy.Chua_Duyet) {
                // Chỉ chuyển sang trạng thái đã hủy
                dangKyHoatDong.setTrangThaiDangKy(DangKyHoatDong.TrangThaiDangKy.Da_Huy);
                dangKyHoatDong.setLyDoHuy(huyHoatDongRequest.getLyDoHuy());
            } else if (dangKyHoatDong.getTrangThaiDangKy() == DangKyHoatDong.TrangThaiDangKy.Da_Duyet) {
                // Chuyển sang trạng thái đã hủy và trừ giờ tích lũy
                dangKyHoatDong.setTrangThaiDangKy(DangKyHoatDong.TrangThaiDangKy.Da_Huy);
                dangKyHoatDong.setLyDoHuy(huyHoatDongRequest.getLyDoHuy());
                GiangVien giangVien = dangKyHoatDong.getGiangVien();
                if (giangVien != null) {
                    int gioTichLuyThamGia = dangKyHoatDong.getHoatDong().getGioTichLuyThamGia();
                    GioTichLuy gioTichLuy = gioTichLuyRepository.findByGiangVien_MaTaiKhoan(giangVien.getMaTaiKhoan());
                    if (gioTichLuy != null && gioTichLuy.getTongSoGio() >= gioTichLuyThamGia) {
                        gioTichLuy.setTongSoGio(gioTichLuy.getTongSoGio() - gioTichLuyThamGia);
                        gioTichLuyRepository.save(gioTichLuy);
                    }
                }

                List<GiangVien> giangVienToChucs = dangKyHoatDong.getHoatDong().getGiangVienToChucs();
                for (GiangVien giangVienToChuc : giangVienToChucs) {
                    int gioTichLuyToChuc = dangKyHoatDong.getHoatDong().getGioTichLuyToChuc();
                    GioTichLuy gioTichLuyToChucEntity = gioTichLuyRepository.findByGiangVien_MaTaiKhoan(giangVienToChuc.getMaTaiKhoan());

                    if (gioTichLuyToChucEntity != null && gioTichLuyToChucEntity.getTongSoGio() >= gioTichLuyToChuc) {
                        gioTichLuyToChucEntity.setTongSoGio(gioTichLuyToChucEntity.getTongSoGio() - gioTichLuyToChuc);
                        gioTichLuyRepository.save(gioTichLuyToChucEntity);
                    }
                }
            }
        }else {
            return ResponseEntity.badRequest().body("Lý do hủy không được trống");
        }

        dangKyHoatDongRepository.save(dangKyHoatDong);
        return ResponseEntity.ok("Hủy đăng ký hoạt động thành công.");
    }


}
