package com.quanly.hoatdongcongdong.repository;

import com.quanly.hoatdongcongdong.entity.CauHoi;
import com.quanly.hoatdongcongdong.entity.SinhVien;
import com.quanly.hoatdongcongdong.entity.TaiKhoan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SinhVienRepository extends JpaRepository<SinhVien, Long>, JpaSpecificationExecutor<SinhVien> {
    Page<SinhVien> findAll(Specification<SinhVien> spec, Pageable pageable);
}
