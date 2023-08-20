package com.quanly.hoatdongcongdong.service;

import com.quanly.hoatdongcongdong.entity.SinhVien;
import com.quanly.hoatdongcongdong.repository.SinhVienRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SinhVienService {

    private final SinhVienRepository sinhVienRepository;

    @Autowired
    public SinhVienService(SinhVienRepository sinhVienRepository) {
        this.sinhVienRepository = sinhVienRepository;
    }

    public Optional<SinhVien> findById(Long id) {
        return sinhVienRepository.findById(id);
    }
    public Page<SinhVien> findAllWithSpec(Specification<SinhVien> spec, Pageable pageable) {
        return sinhVienRepository.findAll(spec, pageable);
    }
}

