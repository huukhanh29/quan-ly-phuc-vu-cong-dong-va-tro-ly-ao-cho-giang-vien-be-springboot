package com.quanly.hoatdongcongdong.service;

import com.quanly.hoatdongcongdong.entity.LichSu;
import com.quanly.hoatdongcongdong.payload.response.LichSuResponse;
import com.quanly.hoatdongcongdong.repository.LichSuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;
@Service
public class LichSuService {

    private final LichSuRepository lichSuRepository;

    @Autowired
    public LichSuService(LichSuRepository lichSuRepository) {
        this.lichSuRepository = lichSuRepository;
    }

    public List<LichSu> findBySinhVien_MaTaiKhoan(Long maTaiKhoan) {
        return lichSuRepository.findBySinhVien_MaTaiKhoan(maTaiKhoan);
    }

    public Page<LichSu> findAll(Pageable pageable) {
        return lichSuRepository.findAll(pageable);
    }



    public LichSu findFirstByCauHoi_MaCauHoi(Long maCauHoi) {
        return lichSuRepository.findFirstByCauHoi_MaCauHoi(maCauHoi);
    }
    public LichSu saveLichSu(LichSu lichSu) {
        return lichSuRepository.save(lichSu);
    }

    public Page<LichSuResponse> getLichSuByUserId(Long userId, int page, int size, String sortBy, String sortDir, String searchTerm) {
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

    public Map<String, Object> getChart(int year) {
        LocalDate startOfYear = LocalDate.of(year, 1, 1);
        LocalDate endOfYear = LocalDate.of(year, 12, 31);
        List<LichSu> lichSuEntities = lichSuRepository.findByNgayTaoBetween(startOfYear.atStartOfDay(),
                endOfYear.atTime(LocalTime.MAX));

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

        return chartData;
    }
    public List<Integer> getDistinctYears() {
        List<Integer> years = lichSuRepository.findDistinctNam();
        return years;
    }
}
