package com.quanly.hoatdongcongdong.service;

import com.quanly.hoatdongcongdong.entity.GiangVien;
import com.quanly.hoatdongcongdong.repository.GiangVienRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GiangVienService {

    private final GiangVienRepository giangVienRepository;

    @Autowired
    public GiangVienService(GiangVienRepository giangVienRepository) {
        this.giangVienRepository = giangVienRepository;
    }

    public List<GiangVien> findByMaTaiKhoanIn(List<Long> maTaiKhoanList) {
        return giangVienRepository.findByMaTaiKhoanIn(maTaiKhoanList);
    }
    public Optional<GiangVien> findById(Long maTaiKhoan) {
        return giangVienRepository.findById(maTaiKhoan);
    }
    public GiangVien saveGiangVien(GiangVien giangVien) {
        return giangVienRepository.save(giangVien);
    }
    public List<GiangVien> findAll(){
        return  giangVienRepository.findAll();
    }
    public Page<GiangVien> findAllWithSpec(Specification<GiangVien> spec, Pageable pageable) {
        return giangVienRepository.findAll(spec, pageable);
    }
    public List<GiangVien> getAllGiangVien() {
        return giangVienRepository.findAll();
    }

}

