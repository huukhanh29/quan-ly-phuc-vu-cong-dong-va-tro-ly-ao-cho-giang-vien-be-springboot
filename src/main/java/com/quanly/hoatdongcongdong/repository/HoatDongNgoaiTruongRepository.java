package com.quanly.hoatdongcongdong.repository;

import com.quanly.hoatdongcongdong.entity.HoatDong;
import com.quanly.hoatdongcongdong.entity.HoatDongNgoaiTruong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HoatDongNgoaiTruongRepository extends JpaRepository<HoatDongNgoaiTruong, Long>, JpaSpecificationExecutor<HoatDongNgoaiTruong> {
    @Query("SELECT DISTINCT YEAR(a.thoiGianBatDau) FROM HoatDongNgoaiTruong a ORDER BY YEAR(a.thoiGianBatDau) ASC")
    List<Integer> findYears();
}
