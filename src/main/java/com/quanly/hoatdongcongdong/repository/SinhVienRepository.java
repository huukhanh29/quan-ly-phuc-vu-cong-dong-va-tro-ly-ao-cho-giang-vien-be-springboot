package com.quanly.hoatdongcongdong.repository;

import com.quanly.hoatdongcongdong.entity.SinhVien;
import com.quanly.hoatdongcongdong.entity.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SinhVienRepository extends JpaRepository<SinhVien, Long>, JpaSpecificationExecutor<SinhVien> {
    // Các phương thức tùy chỉnh nếu cần
}
