package com.quanly.hoatdongcongdong.service;

import com.quanly.hoatdongcongdong.entity.*;
import com.quanly.hoatdongcongdong.payload.request.HuyHoatDongRequest;
import com.quanly.hoatdongcongdong.repository.DangKyHoatDongRepository;
import com.quanly.hoatdongcongdong.repository.GioTichLuyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DangKyHoatDongService {

    private final DangKyHoatDongRepository dangKyHoatDongRepository;
    private final GioTichLuyRepository gioTichLuyRepository;

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
            Long userId
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
        if (!year.isEmpty()) {
            LocalDateTime startOfYear = LocalDateTime.of(Integer.parseInt(year), 1, 1, 0, 0, 0);
            LocalDateTime endOfYear = LocalDateTime.of(Integer.parseInt(year), 12, 31, 23, 59, 59);
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.between(root.get("hoatDong").get("thoiGianBatDau"), startOfYear, endOfYear));
        }
        if (userId != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("giangVien").get("maTaiKhoan"), userId));
        }

        return dangKyHoatDongRepository.findAll(spec, paging);
    }

    public void dangKyHoatDong(HoatDong hoatDong, GiangVien giangVien) {
        DangKyHoatDong dangKyHoatDong = new DangKyHoatDong();
        dangKyHoatDong.setGiangVien(giangVien);
        dangKyHoatDong.setHoatDong(hoatDong);
        dangKyHoatDong.setTrangThaiDangKy(DangKyHoatDong.TrangThaiDangKy.Chua_Duyet);
        dangKyHoatDongRepository.save(dangKyHoatDong);
    }

    public void approveDangKyHoatDong(DangKyHoatDong dangKyHoatDong) {


        // Thực hiện phê duyệt đăng ký hoạt động
        dangKyHoatDong.setTrangThaiDangKy(DangKyHoatDong.TrangThaiDangKy.Da_Duyet);
        dangKyHoatDongRepository.save(dangKyHoatDong);

        GiangVien giangVien = dangKyHoatDong.getGiangVien();
        LocalDateTime thoiGianBatDau = dangKyHoatDong.getHoatDong().getThoiGianBatDau();
        int namBatDau = thoiGianBatDau.getYear();
        int namKetThuc;

        // Determine namKetThuc based on thoiGianBatDau
        if (thoiGianBatDau.getMonthValue() >= 7) {
            namKetThuc = namBatDau + 1;
        } else {
            namKetThuc = namBatDau;
            namBatDau = namKetThuc - 1;
        }
        String namHoc = namBatDau + "-" + namKetThuc;

        // Update gioTichLuy for the giangVien
        int gioTichLuyThamGia = dangKyHoatDong.getHoatDong().getGioTichLuyThamGia();
        GioTichLuy gioTichLuy = gioTichLuyRepository.findByGiangVien_MaTaiKhoan(giangVien.getMaTaiKhoan());

        if (gioTichLuy == null) {
            gioTichLuy = new GioTichLuy();
            gioTichLuy.setGiangVien(giangVien);
            gioTichLuy.setTongSoGio(gioTichLuyThamGia);
            gioTichLuy.setNamHoc(namHoc);
        } else {
            gioTichLuy.setTongSoGio(gioTichLuy.getTongSoGio() + gioTichLuyThamGia);
        }
        gioTichLuyRepository.save(gioTichLuy);

        // Update gioTichLuyToChuc for each GiangVienToChuc
        List<GiangVien> giangVienToChucs = dangKyHoatDong.getHoatDong().getGiangVienToChucs();

        for (GiangVien giangVienToChuc : giangVienToChucs) {
            int gioTichLuyToChuc = dangKyHoatDong.getHoatDong().getGioTichLuyToChuc();
            GioTichLuy gioTichLuyToChucEntity = gioTichLuyRepository.findByGiangVien_MaTaiKhoan(giangVienToChuc.getMaTaiKhoan());

            if (gioTichLuyToChucEntity == null) {
                gioTichLuyToChucEntity = new GioTichLuy();
                gioTichLuyToChucEntity.setGiangVien(giangVienToChuc);
                gioTichLuyToChucEntity.setTongSoGio(gioTichLuyToChuc);
                gioTichLuyToChucEntity.setNamHoc(namHoc);
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
            // Xử lý trừ giờ tích lũy cho giảng viên
            GiangVien giangVien = dangKyHoatDong.getGiangVien();
            if (giangVien != null) {
                int gioTichLuyThamGia = dangKyHoatDong.getHoatDong().getGioTichLuyThamGia();
                GioTichLuy gioTichLuy = gioTichLuyRepository.findByGiangVien_MaTaiKhoan(giangVien.getMaTaiKhoan());
                if (gioTichLuy != null && gioTichLuy.getTongSoGio() >= gioTichLuyThamGia) {
                    gioTichLuy.setTongSoGio(gioTichLuy.getTongSoGio() - gioTichLuyThamGia);
                    gioTichLuyRepository.save(gioTichLuy);
                }
            }
            // Xử lý trừ giờ tích lũy cho giảng viên tổ chức
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
        dangKyHoatDongRepository.save(dangKyHoatDong);
    }
}
