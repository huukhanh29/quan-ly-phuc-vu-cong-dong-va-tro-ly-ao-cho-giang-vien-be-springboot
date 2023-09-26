package com.quanly.hoatdongcongdong.service;

import com.quanly.hoatdongcongdong.entity.CauHoi;
import com.quanly.hoatdongcongdong.exception.ResourceNotFoundException;
import com.quanly.hoatdongcongdong.repository.CauHoiRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CauHoiService {

    private final CauHoiRepository cauHoiRepository;
    @Autowired
    public CauHoiService(CauHoiRepository cauHoiRepository) {
        this.cauHoiRepository = cauHoiRepository;
    }

    public CauHoi findByCauHoi(String cauHoi) {
        return cauHoiRepository.findByCauHoi(cauHoi);
    }

    public Page<CauHoi> findAll(Pageable pageable) {
        return cauHoiRepository.findAll(pageable);
    }
    public List<CauHoi> findAllNoPage() {
        return cauHoiRepository.findAll();
    }
    public boolean existsByCauHoi(String cauhoi) {
        return cauHoiRepository.existsByCauHoi(cauhoi);
    }
    public CauHoi saveCauHoi(CauHoi cauHoi) {
        return cauHoiRepository.save(cauHoi);
    }
    public Page<CauHoi> getAllCauHoi(int page, int size, String sortBy, String sortDir, String searchTerm) {
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(Sort.Direction.fromString(sortDir), sortBy));
        if (!sortBy.equalsIgnoreCase("cauHoi")) {
            orders.add(new Sort.Order(Sort.Direction.ASC, "cauHoi"));
        }
        Sort sort = Sort.by(orders);

        Pageable paging = PageRequest.of(page, size, sort);

        Specification<CauHoi> spec = Specification.where(null);

        if (!searchTerm.isEmpty()) {
            spec = spec.and((root, criteriaQuery, criteriaBuilder) -> {
                String pattern = "%" + searchTerm + "%";
                return criteriaBuilder.or(criteriaBuilder.like(root.get("cauHoi"), pattern));
            });
        }

        return cauHoiRepository.findAll(spec, paging);
    }

    public Optional<CauHoi> getCauHoiById(Long cauHoiId) {
        return cauHoiRepository.findById(cauHoiId);
    }
    public CauHoi updateCauHoi(Long cauHoiId, CauHoi cauHoiDetails) {
        Optional<CauHoi> optionalCauHoi = cauHoiRepository.findById(cauHoiId);

        if (optionalCauHoi.isPresent()) {
            CauHoi existingCauHoi = optionalCauHoi.get();
            existingCauHoi.setCauHoi(cauHoiDetails.getCauHoi());
            existingCauHoi.setTraLoi(cauHoiDetails.getTraLoi());
            return cauHoiRepository.save(existingCauHoi);
        } else {
            throw new EntityNotFoundException("Không tìm thấy câu hỏi!");
        }
    }

    public Long countCauHoi() {
        return cauHoiRepository.count();
    }

    public void deleteCauHoiById(Long cauHoiId) {
        Optional<CauHoi> optionalCauHoi = cauHoiRepository.findById(cauHoiId);
        if (optionalCauHoi.isPresent()) {
            cauHoiRepository.deleteById(cauHoiId);
        } else {
            throw new EntityNotFoundException("Không tìm thấy câu hỏi!");
        }
    }
    @Transactional
    public List<CauHoi> saveAll(List<CauHoi> cauHoiList) {
        return cauHoiRepository.saveAll(cauHoiList);
    }

}

