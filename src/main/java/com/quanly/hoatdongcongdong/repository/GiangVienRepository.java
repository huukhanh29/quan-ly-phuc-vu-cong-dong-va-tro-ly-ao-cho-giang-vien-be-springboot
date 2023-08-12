package com.quanly.hoatdongcongdong.repository;

import com.quanly.hoatdongcongdong.entity.GiangVien;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GiangVienRepository extends JpaRepository<GiangVien, Long> {
    // Các phương thức tùy chỉnh nếu cần
}
