package com.quanly.hoatdongcongdong.repository;

import com.quanly.hoatdongcongdong.entity.ChucDanh;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChucDanhRepository extends JpaRepository<ChucDanh, Long> {
    // Các phương thức tùy chỉnh nếu cần
}

