package com.quanly.hoatdongcongdong.repository;

import com.quanly.hoatdongcongdong.entity.GiangVien;
import com.quanly.hoatdongcongdong.entity.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GiangVienRepository extends JpaRepository<GiangVien, Long> , JpaSpecificationExecutor<GiangVien> {
    // Các phương thức tùy chỉnh nếu cần
}
