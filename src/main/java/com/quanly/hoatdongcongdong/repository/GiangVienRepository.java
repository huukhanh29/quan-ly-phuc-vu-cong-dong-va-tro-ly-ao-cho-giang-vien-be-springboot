package com.quanly.hoatdongcongdong.repository;

import com.quanly.hoatdongcongdong.entity.CauHoi;
import com.quanly.hoatdongcongdong.entity.GiangVien;
import com.quanly.hoatdongcongdong.entity.TaiKhoan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface GiangVienRepository extends JpaRepository<GiangVien, Long> , JpaSpecificationExecutor<GiangVien> {
    List<GiangVien> findByMaTaiKhoanIn(List<Long> maTaiKhoanList);
    List<GiangVien> findByTaiKhoan_TenDangNhapIn(List<String> tenDn);
    Page<GiangVien> findAll(Specification<GiangVien> spec, Pageable pageable);
}
