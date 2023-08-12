package com.quanly.hoatdongcongdong.repository;

import com.quanly.hoatdongcongdong.entity.SinhVien;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SinhVienRepository extends JpaRepository<SinhVien, Long> {
    // Các phương thức tùy chỉnh nếu cần
}
