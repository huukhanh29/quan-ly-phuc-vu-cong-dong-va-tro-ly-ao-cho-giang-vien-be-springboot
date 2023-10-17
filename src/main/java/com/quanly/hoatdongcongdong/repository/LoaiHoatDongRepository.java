package com.quanly.hoatdongcongdong.repository;

import com.quanly.hoatdongcongdong.entity.LoaiHoatDong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface LoaiHoatDongRepository extends JpaRepository<LoaiHoatDong, Long> {
        boolean existsByTenLoaiHoatDong(String ten);
}
