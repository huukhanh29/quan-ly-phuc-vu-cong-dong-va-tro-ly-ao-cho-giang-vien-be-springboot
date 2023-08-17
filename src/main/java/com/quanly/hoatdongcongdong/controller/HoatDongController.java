package com.quanly.hoatdongcongdong.controller;

import com.quanly.hoatdongcongdong.entity.HoatDong;
import com.quanly.hoatdongcongdong.payload.response.HoatDongResponse;
import com.quanly.hoatdongcongdong.repository.DangKyHoatDongRepository;
import com.quanly.hoatdongcongdong.repository.GiangVienRepository;
import com.quanly.hoatdongcongdong.repository.HoatDongRepository;
import com.quanly.hoatdongcongdong.repository.LoaiHoatDongRepository;
import com.quanly.hoatdongcongdong.sercurity.services.TaiKhoanService;
import com.quanly.hoatdongcongdong.sercurity.services.ThongBaoService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/hoat-dong")
@CrossOrigin(value = "*")
public class HoatDongController {
    @Autowired
    private HoatDongRepository hoatDongRepository;
    @Autowired
    private DangKyHoatDongRepository dangKyHoatDongRepository;
    @Autowired
    private LoaiHoatDongRepository loaiHoatDongRepository;
    @Autowired
    private GiangVienRepository giangVienRepository;
    @Autowired
    private TaiKhoanService taiKhoanService;
    @Autowired
    private ThongBaoService thongBaoService;

    @GetMapping("/{maHoatDong}")
    public ResponseEntity<HoatDong> getHoatDongById(@PathVariable Long maHoatDong) {
        // Tìm hoạt động theo mã hoạt động
        HoatDong hoatDong = hoatDongRepository.findById(maHoatDong)
                .orElse(null);

        if (hoatDong == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(hoatDong);
    }

    @GetMapping("/lay-danh-sach")
    public Page<HoatDong> getAllHoatDong(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ngayTao") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir,
            @RequestParam(required = false, defaultValue = "") String searchTerm,
            @RequestParam(required = false, defaultValue = "") String type,
            @RequestParam(required = false) HoatDong.TrangThaiHoatDong status,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime
//            @RequestParam(required = false) Long userId
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable paging = PageRequest.of(page, size, sort);

        Specification<HoatDong> spec = Specification.where(null);

        if (!searchTerm.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.or(
                            criteriaBuilder.like(root.get("tenHoatDong"), "%" + searchTerm + "%"),
                            criteriaBuilder.like(root.get("moTa"), "%" + searchTerm + "%")
                    ));
        }
        if (status != null) {
            spec = spec.and((root, criteriaQuery, criteriaBuilder)
                    -> criteriaBuilder.equal(root.get("trangThaiHoatDong"), status));
        }
        if (!type.isEmpty()) {
            spec = spec.and((root, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("loaiHoatDong").get("tenLoaiHoatDong"), type));
        }

        if (startTime != null) {
            LocalDateTime startTimes = LocalDate.parse(startTime).atStartOfDay();
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("thoiGianBatDau"), startTimes));
        }

        if (endTime != null) {
            LocalDate date = LocalDate.parse(endTime);
            LocalDateTime endTimes = date.atTime(23, 59, 59);
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("thoiGianKetThuc"), endTimes));
        }

//        if (userId != null) {
//            List<DangKyHoatDong> userActivities = dangKyHoatDongRepository.findByGiangVien_MaTaiKhoan(userId);
//            List<Long> activityIds = userActivities.stream()
//                    .map(ua -> ua.getHoatDong().getMaHoatDong())
//                    .collect(Collectors.toList());
//            spec = spec.and((root, query, criteriaBuilder) ->
//                    root.get("maHoatDong").in(activityIds));
//        }

        return hoatDongRepository.findAll(spec, paging);
    }

    //==== chưa hoàn thành======
    @PostMapping("/them-moi")
    public ResponseEntity<String> addHoatDong(@RequestBody HoatDongResponse hoatDongResponse) {

        // Kiểm tra nếu loại hoạt động không tồn tại
        Optional<LoaiHoatDong> loaiHoatDong = loaiHoatDongRepository.findById(hoatDongResponse.getMaLoaiHoatDong());
        if (!loaiHoatDong.isPresent()) {
            return ResponseEntity.badRequest().body("Loại hoạt động không tồn tại");
        }

        HoatDong hoatDong = new HoatDong();
        hoatDong.setTenHoatDong(hoatDongResponse.getTenHoatDong());
        hoatDong.setMoTa(hoatDongResponse.getMoTa());
        hoatDong.setDiaDiem(hoatDongResponse.getDiaDiem());
        hoatDong.setGioTichLuyThamGia(hoatDongResponse.getGioTichLuyThamGia());
        hoatDong.setGioTichLuyToChuc(hoatDongResponse.getGioTichLuyToChuc());
        hoatDong.setThoiGianBatDau(hoatDongResponse.getThoiGianBatDau());
        hoatDong.setThoiGianKetThuc(hoatDongResponse.getThoiGianKetThuc());
        hoatDong.setLoaiHoatDong(loaiHoatDong.get());

        List<GiangVien> giangVienToChucs = giangVienRepository.findByMaTaiKhoanIn(hoatDongResponse.getMaGiangVienToChucs());
        hoatDong.setGiangVienToChucs(giangVienToChucs);
        hoatDong.setTenQuyetDinh(hoatDongResponse.getTenQuyetDinh());
        hoatDong.setSoQuyetDinh(hoatDongResponse.getSoQuyetDinh());
        hoatDong.setCapToChuc(hoatDongResponse.getCapToChuc());
        hoatDong.setFileQuyetDinh(hoatDongResponse.getFileQuyetDinh());
        hoatDong.setNguoiKyQuyetDinh(hoatDongResponse.getNguoiKyQuyetDinh());
        // Lưu hoạt động vào cơ sở dữ liệu
        hoatDongRepository.save(hoatDong);

        return ResponseEntity.ok("Hoạt động đã được thêm thành công");
    }

    @PutMapping("/cap-nhat/{maHoatDong}")
    public ResponseEntity<String> updateHoatDong(@PathVariable Long maHoatDong,
                                                 @RequestBody HoatDongResponse hoatDongResponse) {

        // Kiểm tra hoạt động có tồn tại không
        Optional<HoatDong> existingHoatDong = hoatDongRepository.findById(maHoatDong);
        if (!existingHoatDong.isPresent()) {
            return ResponseEntity.badRequest().body("Hoạt động không tồn tại");
        }

        // Kiểm tra nếu loại hoạt động không tồn tại
        Optional<LoaiHoatDong> loaiHoatDong = loaiHoatDongRepository.findById(hoatDongResponse.getMaLoaiHoatDong());
        if (loaiHoatDong.isEmpty()) {
            return ResponseEntity.badRequest().body("Loại hoạt động không tồn tại");
        }

        HoatDong hoatDong = existingHoatDong.get();
        hoatDong.setTenHoatDong(hoatDongResponse.getTenHoatDong());
        hoatDong.setMoTa(hoatDongResponse.getMoTa());
        hoatDong.setDiaDiem(hoatDongResponse.getDiaDiem());
        hoatDong.setGioTichLuyThamGia(hoatDongResponse.getGioTichLuyThamGia());
        hoatDong.setGioTichLuyToChuc(hoatDongResponse.getGioTichLuyToChuc());
        hoatDong.setThoiGianBatDau(hoatDongResponse.getThoiGianBatDau());
        hoatDong.setThoiGianKetThuc(hoatDongResponse.getThoiGianKetThuc());
        hoatDong.setLoaiHoatDong(loaiHoatDong.get());

        List<GiangVien> giangVienToChucs = giangVienRepository.findByMaTaiKhoanIn(hoatDongResponse.getMaGiangVienToChucs());
        hoatDong.setGiangVienToChucs(giangVienToChucs);
        hoatDong.setTenQuyetDinh(hoatDongResponse.getTenQuyetDinh());
        hoatDong.setSoQuyetDinh(hoatDongResponse.getSoQuyetDinh());
        hoatDong.setCapToChuc(hoatDongResponse.getCapToChuc());
        hoatDong.setFileQuyetDinh(hoatDongResponse.getFileQuyetDinh());
        hoatDong.setNguoiKyQuyetDinh(hoatDongResponse.getNguoiKyQuyetDinh());

        // Cập nhật hoạt động vào cơ sở dữ liệu
        hoatDongRepository.save(hoatDong);

        return ResponseEntity.ok("Hoạt động đã được cập nhật thành công");
    }

    @DeleteMapping("/xoa/{maHoatDong}")
    public ResponseEntity<String> deleteHoatDong(
            @PathVariable Long maHoatDong) {

        // Kiểm tra xem hoạt động đã được lưu trữ trong bảng DangKyHoatDong hay chưa
        if (dangKyHoatDongRepository.existsByHoatDong_MaHoatDong(maHoatDong)) {
            return ResponseEntity.badRequest().body("Không thể xóa hoạt động đã có dữ liệu đăng ký");
        }

        // Xóa hoạt động nếu không có dữ liệu đăng ký
        hoatDongRepository.deleteById(maHoatDong);

        return ResponseEntity.ok("Hoạt động đã được xóa thành công");
    }

    @GetMapping("/lay-danh-sach-nam")
    public List<Integer> getYears() {
        return hoatDongRepository.findYears();
    }
    @GetMapping("/so-hoat-dong-chua-dien-ra")
    public Long countUpcomingActivities() {
        return hoatDongRepository.countByTrangThaiHoatDong(HoatDong.TrangThaiHoatDong.SAP_DIEN_RA);
    }
}
