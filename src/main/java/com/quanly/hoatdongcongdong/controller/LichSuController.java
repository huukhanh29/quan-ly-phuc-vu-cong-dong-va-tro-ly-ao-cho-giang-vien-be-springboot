package com.quanly.hoatdongcongdong.controller;
import com.quanly.hoatdongcongdong.entity.CauHoi;
import com.quanly.hoatdongcongdong.entity.LichSu;
import com.quanly.hoatdongcongdong.entity.SinhVien;
import com.quanly.hoatdongcongdong.entity.TaiKhoan;
import com.quanly.hoatdongcongdong.payload.response.LichSuResponse;
import com.quanly.hoatdongcongdong.repository.LichSuRepository;
import com.quanly.hoatdongcongdong.sercurity.services.TaiKhoanService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/lich-su")
@CrossOrigin(value = "*")
public class LichSuController {
    @Autowired
    private LichSuRepository lichSuRepository;
    @Autowired
    private TaiKhoanService taiKhoanService;
    @GetMapping
    public Page<LichSuResponse> lichSu(HttpServletRequest httpServletRequest,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size,
                                       @RequestParam(defaultValue = "maLichSu") String sortBy,
                                       @RequestParam(defaultValue = "DESC") String sortDir,
                                       @RequestParam(required = false, defaultValue = "") String searchTerm) {
        Long userId =  taiKhoanService.getCurrentUser(httpServletRequest).getMaTaiKhoan();
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable paging = PageRequest.of(page, size, sort);
        Specification<LichSu> spec = Specification.where(null);

        if (!searchTerm.isEmpty()) {
            spec = spec.and((root, criteriaQuery, criteriaBuilder) -> {
                String pattern = "%" + searchTerm + "%";
                return criteriaBuilder.or(
                        criteriaBuilder.like(root.get("cauHoi").get("cauHoi"), pattern)
                );
            });
        }
        if (userId != null) {
            spec = spec.and((root, criteriaQuery, criteriaBuilder) -> {
                return criteriaBuilder.equal(root.get("sinhVien").get("maTaiKhoan"), userId);
            });
        }

        Page<LichSu> lichSuEntities = lichSuRepository.findAll(spec, paging);

        return lichSuEntities.map(LichSuResponse::fromEntity);
    }

//    @PostMapping("/luu-lich-su/{maTk}/cau-hoi/{cauHoiId}")
//    public LichSu createUserCauHoi(@PathVariable Long maTk, @PathVariable Long cauHoiId) {
//        LichSu lichSu = new LichSu();
//        SinhVien sinhVien = new SinhVien();
//        sinhVien.setMaTaiKhoan(maTk);
//        lichSu.setSinhVien(sinhVien);
//        CauHoi cauHoi = new CauHoi();
//        cauHoi.setMaCauHoi(cauHoiId);
//        lichSu.setCauHoi(cauHoi);
//        return lichSuRepository.save(lichSu);
//    }

    @GetMapping("/bieu-do-luot-hoi")
    public ResponseEntity<Map<String, Object>> getChart(@RequestParam("year") int year) {
        LocalDate startOfYear = LocalDate.of(year, 1, 1);
        LocalDate endOfYear = LocalDate.of(year, 12, 31);
        List<LichSu> lichSuEntities = lichSuRepository.findByNgayTaoBetween(startOfYear.atStartOfDay(), endOfYear.atTime(LocalTime.MAX));
        Map<Month, Long> groupedByMonth = lichSuEntities.stream()
                .collect(Collectors.groupingBy(
                        lichSu -> lichSu.getNgayTao().getMonth(),
                        Collectors.counting()));

        List<String> labels = Arrays.stream(Month.values())
                .map(month -> month.getDisplayName(TextStyle.FULL_STANDALONE, Locale.ENGLISH))
                .collect(Collectors.toList());

        List<Long> data = Arrays.stream(Month.values())
                .map(month -> groupedByMonth.getOrDefault(month, 0L))
                .collect(Collectors.toList());

        Map<String, Object> chartData = new HashMap<>();
        chartData.put("labels", labels);
        chartData.put("data", data);

        return ResponseEntity.ok().body(chartData);
    }

    @GetMapping("/danh-sach-nam-cua-lich-su")
    public ResponseEntity<List<Integer>> getYears() {
        List<Integer> years = lichSuRepository.findDistinctNam();
        return ResponseEntity.ok().body(years);
    }
}
