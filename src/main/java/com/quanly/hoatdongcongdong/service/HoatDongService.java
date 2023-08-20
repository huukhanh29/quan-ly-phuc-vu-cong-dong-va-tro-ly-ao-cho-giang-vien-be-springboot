package com.quanly.hoatdongcongdong.service;

import com.quanly.hoatdongcongdong.entity.GiangVien;
import com.quanly.hoatdongcongdong.entity.HoatDong;
import com.quanly.hoatdongcongdong.entity.LoaiHoatDong;
import com.quanly.hoatdongcongdong.exception.ResourceNotFoundException;
import com.quanly.hoatdongcongdong.payload.response.HoatDongResponse;
import com.quanly.hoatdongcongdong.repository.DangKyHoatDongRepository;
import com.quanly.hoatdongcongdong.repository.GiangVienRepository;
import com.quanly.hoatdongcongdong.repository.HoatDongRepository;
import com.quanly.hoatdongcongdong.repository.LoaiHoatDongRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class HoatDongService {

    private final HoatDongRepository hoatDongRepository;
    private final LoaiHoatDongRepository loaiHoatDongRepository;
    private final GiangVienRepository giangVienRepository;
    private final DangKyHoatDongRepository dangKyHoatDongRepository;

    @Autowired
    public HoatDongService(
            HoatDongRepository hoatDongRepository,
            LoaiHoatDongRepository loaiHoatDongRepository,
            GiangVienRepository giangVienRepository,
            DangKyHoatDongRepository dangKyHoatDongRepository
    ) {
        this.hoatDongRepository = hoatDongRepository;
        this.loaiHoatDongRepository = loaiHoatDongRepository;
        this.giangVienRepository = giangVienRepository;
        this.dangKyHoatDongRepository =dangKyHoatDongRepository;
    }

    public List<Integer> findYears() {
        return hoatDongRepository.findYears();
    }

    public Long countByTrangThaiHoatDong(HoatDong.TrangThaiHoatDong trangThaiHoatDong) {
        return hoatDongRepository.countByTrangThaiHoatDong(trangThaiHoatDong);
    }

    public Optional<HoatDong> findById(Long maHoatDong) {
        return hoatDongRepository.findById(maHoatDong);
    }
    public Page<HoatDong> getAllHoatDong(
            int page,
            int size,
            String sortBy,
            String sortDir,
            String searchTerm,
            String type,
            HoatDong.TrangThaiHoatDong status,
            String startTime,
            String endTime
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

        return hoatDongRepository.findAll(spec, paging);
    }

    public String addHoatDong(HoatDongResponse hoatDongResponse) {

        // Kiểm tra nếu loại hoạt động không tồn tại
        Optional<LoaiHoatDong> loaiHoatDong = loaiHoatDongRepository.findById(hoatDongResponse.getMaLoaiHoatDong());
        if (loaiHoatDong.isEmpty()) {
            return "Loại hoạt động không tồn tại";
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

        hoatDongRepository.save(hoatDong);

        return "Hoạt động đã được thêm thành công";
    }
    public String updateHoatDong(Long maHoatDong, HoatDongResponse hoatDongResponse) {

        // Kiểm tra hoạt động có tồn tại không
        Optional<HoatDong> existingHoatDong = hoatDongRepository.findById(maHoatDong);
        if (existingHoatDong.isEmpty()) {
            return "Hoạt động không tồn tại";
        }

        // Kiểm tra nếu loại hoạt động không tồn tại
        Optional<LoaiHoatDong> loaiHoatDong = loaiHoatDongRepository.findById(hoatDongResponse.getMaLoaiHoatDong());
        if (loaiHoatDong.isEmpty()) {
            return "Loại hoạt động không tồn tại";
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

        hoatDongRepository.save(hoatDong);

        return "Hoạt động đã được cập nhật thành công";
    }
    public String deleteHoatDong(Long maHoatDong) {

        // Kiểm tra xem hoạt động đã được lưu trữ trong bảng DangKyHoatDong hay chưa
        if (dangKyHoatDongRepository.existsByHoatDong_MaHoatDong(maHoatDong)) {
            return "Không thể xóa hoạt động đã có dữ liệu đăng ký";
        }

        // Xóa hoạt động nếu không có dữ liệu đăng ký
        hoatDongRepository.deleteById(maHoatDong);

        return "Hoạt động đã được xóa thành công";
    }
    public void deleteHoatDongById(Long maHoatDong) {

        Optional<HoatDong> hoatDong = hoatDongRepository.findById(maHoatDong);
        if (hoatDong.isPresent()) {
            hoatDongRepository.deleteById(maHoatDong);
        } else {
            throw new EntityNotFoundException("Không tìm thấy hoạt động!");
        }

    }
    public List<Integer> getYears() {
        return hoatDongRepository.findYears();
    }

    public Long countUpcomingActivities() {
        return hoatDongRepository.countByTrangThaiHoatDong(HoatDong.TrangThaiHoatDong.SAP_DIEN_RA);
    }
}

