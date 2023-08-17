package com.quanly.hoatdongcongdong.repository;

import com.quanly.hoatdongcongdong.entity.HoatDong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface HoatDongRepository extends JpaRepository<HoatDong, Long>, JpaSpecificationExecutor<HoatDong> {
    @Query("SELECT DISTINCT YEAR(a.thoiGianBatDau) FROM HoatDong a ORDER BY YEAR(a.thoiGianBatDau) ASC")
    List<Integer> findYears();

    Long countByTrangThaiHoatDong(HoatDong.TrangThaiHoatDong trangThaiHoatDong);
}

