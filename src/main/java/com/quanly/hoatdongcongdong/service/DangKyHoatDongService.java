package com.quanly.hoatdongcongdong.service;

import com.quanly.hoatdongcongdong.entity.*;
import com.quanly.hoatdongcongdong.payload.request.HuyHoatDongRequest;
import com.quanly.hoatdongcongdong.repository.DangKyHoatDongRepository;
import com.quanly.hoatdongcongdong.repository.GioTichLuyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class DangKyHoatDongService {

    private final DangKyHoatDongRepository dangKyHoatDongRepository;
    private final GioTichLuyRepository gioTichLuyRepository;
    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    public DangKyHoatDongService(DangKyHoatDongRepository dangKyHoatDongRepository,
                                 GioTichLuyRepository gioTichLuyRepository) {
        this.dangKyHoatDongRepository = dangKyHoatDongRepository;
        this.gioTichLuyRepository = gioTichLuyRepository;
    }

    public Optional<DangKyHoatDong> findById(Long id) {
        return dangKyHoatDongRepository.findById(id);
    }


    public boolean existsByHoatDong_MaHoatDong(Long maHoatDong) {
        return dangKyHoatDongRepository.existsByHoatDong_MaHoatDong(maHoatDong);
    }
    public List<HoatDong> getHoatDongDangKyByGiangVien(Long maTaiKhoan, int nam) {
        return dangKyHoatDongRepository.findHoatDongByGiangVienAndYear(maTaiKhoan, nam);
    }
    public boolean existsByGiangVien_MaTaiKhoanAndHoatDong_MaHoatDong(Long maTaiKhoan, Long maHoatDong) {
        return dangKyHoatDongRepository.existsByGiangVien_MaTaiKhoanAndHoatDong_MaHoatDong(maTaiKhoan, maHoatDong);
    }

    public Page<DangKyHoatDong> getDanhSachDangKyHoatDong(
            int page,
            int size,
            String sortBy,
            String sortDir,
            String searchTerm,
            String status,
            String startTime,
            String endTime,
            String year,
            String username
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable paging = PageRequest.of(page, size, sort);

        Specification<DangKyHoatDong> spec = Specification.where(null);

        if (!searchTerm.isEmpty()) {
            spec = spec.and((root, criteriaQuery, criteriaBuilder) -> {
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
        if (year != null) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                LocalDateTime startOfYear = LocalDateTime.of(Integer.parseInt(year), 1, 1, 0, 0, 0);
                LocalDateTime endOfYear = LocalDateTime.of(Integer.parseInt(year), 12, 31, 23, 59, 59);
                return criteriaBuilder.between(root.get("hoatDong").get("thoiGianBatDau"), startOfYear, endOfYear);
            });

//            // Tách chuỗi year thành hai phần và chuyển đổi thành số
//            String[] years = year.split("-");
//            int startYear = Integer.parseInt(years[0]);
//            int endYear = Integer.parseInt(years[1]);
//
//            // Tạo LocalDateTime cho tháng 7 ngày 1 của năm bắt đầu và tháng 7 ngày 1 của năm kết thúc
//            LocalDateTime startOfYear = LocalDateTime.of(startYear, 7, 1, 0, 0, 0);
//            LocalDateTime endOfYear = LocalDateTime.of(endYear, 7, 1, 0, 0, 0);
//
//            // Thêm điều kiện lọc vào Specification
//            spec = spec.and((root, query, criteriaBuilder) ->
//                    criteriaBuilder.between(root.get("hoatDong").get("thoiGianBatDau"), startOfYear, endOfYear));
        }

        if (username != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("giangVien").get("taiKhoan").get("tenDangNhap"), username));
        }

        return dangKyHoatDongRepository.findAll(spec, paging);
    }

    public void dangKyHoatDong(HoatDong hoatDong, GiangVien giangVien) {
        messagingTemplate.convertAndSendToUser("admin", "/queue/messages", "register-activity");
        DangKyHoatDong dangKyHoatDong = new DangKyHoatDong();
        dangKyHoatDong.setGiangVien(giangVien);
        dangKyHoatDong.setHoatDong(hoatDong);
        dangKyHoatDong.setTrangThaiDangKy(DangKyHoatDong.TrangThaiDangKy.Chua_Duyet);
        dangKyHoatDongRepository.save(dangKyHoatDong);
    }
    public boolean kiemTraDangKyHoatDong(String ten, Long ma) {
        if(dangKyHoatDongRepository.existsByGiangVien_TaiKhoan_TenDangNhapAndHoatDong_MaHoatDong(ten, ma)){
            return true;
        } else {
            return false;
        }
    }
    public DangKyHoatDong findByGiangVienAndHoatDong(String ten, Long ma){
        DangKyHoatDong dangKyHoatDong = dangKyHoatDongRepository.findByGiangVien_TaiKhoan_TenDangNhapAndHoatDong_MaHoatDong(ten, ma);
        if(dangKyHoatDong != null){
            return dangKyHoatDong;
        }
        return null;
    }

    public void approveDangKyHoatDong(DangKyHoatDong dangKyHoatDong) {
        // Thực hiện phê duyệt đăng ký hoạt động
        dangKyHoatDong.setTrangThaiDangKy(DangKyHoatDong.TrangThaiDangKy.Da_Duyet);
        dangKyHoatDongRepository.save(dangKyHoatDong);
        messagingTemplate.convertAndSendToUser(dangKyHoatDong.getGiangVien().getTaiKhoan().getTenDangNhap(), "/queue/messages", "approve-activity");

        GiangVien giangVien = dangKyHoatDong.getGiangVien();
        LocalDateTime thoiGianBatDau = dangKyHoatDong.getHoatDong().getThoiGianBatDau();
        String namHoc = String.valueOf(thoiGianBatDau.getYear());
//        int namKetThuc;
//
//        // Determine namKetThuc based on thoiGianBatDau
//        if (thoiGianBatDau.getMonthValue() >= 7) {
//            namKetThuc = namBatDau + 1;
//        } else {
//            namKetThuc = namBatDau;
//            namBatDau = namKetThuc - 1;
//        }
//        String namHoc = namBatDau + "-" + namKetThuc;

        // Update gioTichLuy for the giangVien
        int gioTichLuyThamGia = dangKyHoatDong.getHoatDong().getGioTichLuyThamGia();
        String nam = String.valueOf(dangKyHoatDong.getHoatDong().getThoiGianBatDau().getYear());
        GioTichLuy gioTichLuy = gioTichLuyRepository.findByGiangVien_MaTaiKhoanAndNam(dangKyHoatDong.getGiangVien().getMaTaiKhoan(), nam);

        if (gioTichLuy == null) {
            gioTichLuy = new GioTichLuy();
            gioTichLuy.setGiangVien(giangVien);
            gioTichLuy.setTongSoGio(gioTichLuyThamGia);
            gioTichLuy.setNam(namHoc);
        } else {
            gioTichLuy.setTongSoGio(gioTichLuy.getTongSoGio() + gioTichLuyThamGia);
        }
        gioTichLuyRepository.save(gioTichLuy);

        // Update gioTichLuyToChuc for each GiangVienToChuc
        List<GiangVien> giangVienToChucs = dangKyHoatDong.getHoatDong().getGiangVienToChucs();

        for (GiangVien giangVienToChuc : giangVienToChucs) {
            int gioTichLuyToChuc = dangKyHoatDong.getHoatDong().getGioTichLuyToChuc();
            GioTichLuy gioTichLuyToChucEntity = gioTichLuyRepository.findByGiangVien_MaTaiKhoanAndNam(giangVienToChuc.getMaTaiKhoan(), nam);

            if (gioTichLuyToChucEntity == null) {
                gioTichLuyToChucEntity = new GioTichLuy();
                gioTichLuyToChucEntity.setGiangVien(giangVienToChuc);
                gioTichLuyToChucEntity.setTongSoGio(gioTichLuyToChuc);
                gioTichLuyToChucEntity.setNam(namHoc);
            } else {
                gioTichLuyToChucEntity.setTongSoGio(gioTichLuyToChucEntity.getTongSoGio() + gioTichLuyToChuc);
            }
            gioTichLuyRepository.save(gioTichLuyToChucEntity);
        }
    }

    public void huyDangKyHoatDong(DangKyHoatDong dangKyHoatDong, HuyHoatDongRequest huyHoatDongRequest) {
        if (dangKyHoatDong.getTrangThaiDangKy() == DangKyHoatDong.TrangThaiDangKy.Chua_Duyet) {
            dangKyHoatDong.setTrangThaiDangKy(DangKyHoatDong.TrangThaiDangKy.Da_Huy);
            dangKyHoatDong.setLyDoHuy(huyHoatDongRequest.getLyDoHuy());
        } else if (dangKyHoatDong.getTrangThaiDangKy() == DangKyHoatDong.TrangThaiDangKy.Da_Duyet) {
            dangKyHoatDong.setTrangThaiDangKy(DangKyHoatDong.TrangThaiDangKy.Da_Huy);
            dangKyHoatDong.setLyDoHuy(huyHoatDongRequest.getLyDoHuy());
            String nam = String.valueOf(dangKyHoatDong.getHoatDong().getThoiGianBatDau().getYear());
            // Xử lý trừ giờ tích lũy cho giảng viên
            GiangVien giangVien = dangKyHoatDong.getGiangVien();
            if (giangVien != null) {
                int gioTichLuyThamGia = dangKyHoatDong.getHoatDong().getGioTichLuyThamGia();
                GioTichLuy gioTichLuy = gioTichLuyRepository.findByGiangVien_MaTaiKhoanAndNam(giangVien.getMaTaiKhoan(), nam);
                if (gioTichLuy != null && gioTichLuy.getTongSoGio() >= gioTichLuyThamGia) {
                    gioTichLuy.setTongSoGio(gioTichLuy.getTongSoGio() - gioTichLuyThamGia);
                    gioTichLuyRepository.save(gioTichLuy);
                }
            }
            // Xử lý trừ giờ tích lũy cho giảng viên tổ chức
            List<GiangVien> giangVienToChucs = dangKyHoatDong.getHoatDong().getGiangVienToChucs();
            for (GiangVien giangVienToChuc : giangVienToChucs) {
                int gioTichLuyToChuc = dangKyHoatDong.getHoatDong().getGioTichLuyToChuc();
                GioTichLuy gioTichLuyToChucEntity = gioTichLuyRepository.findByGiangVien_MaTaiKhoanAndNam(giangVienToChuc.getMaTaiKhoan(), nam);

                if (gioTichLuyToChucEntity != null && gioTichLuyToChucEntity.getTongSoGio() >= gioTichLuyToChuc) {
                    gioTichLuyToChucEntity.setTongSoGio(gioTichLuyToChucEntity.getTongSoGio() - gioTichLuyToChuc);
                    gioTichLuyRepository.save(gioTichLuyToChucEntity);
                }
            }
        }
        messagingTemplate.convertAndSendToUser(dangKyHoatDong.getGiangVien().getTaiKhoan().getTenDangNhap(), "/queue/messages", "destroy-activity");
        dangKyHoatDongRepository.save(dangKyHoatDong);
    }
}
