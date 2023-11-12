package com.quanly.hoatdongcongdong.repository;

import com.quanly.hoatdongcongdong.entity.HoatDong;
import com.quanly.hoatdongcongdong.entity.HoatDongNgoaiTruong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HoatDongNgoaiTruongRepository extends JpaRepository<HoatDongNgoaiTruong, Long>, JpaSpecificationExecutor<HoatDongNgoaiTruong> {
    @Query("SELECT DISTINCT YEAR(a.thoiGianBatDau) FROM HoatDongNgoaiTruong a ORDER BY YEAR(a.thoiGianBatDau) ASC")
    List<Integer> findYears();
    @Query("SELECT hdn FROM HoatDongNgoaiTruong hdn WHERE hdn.giangVien.maTaiKhoan = :maTaiKhoan AND hdn.trangThai = 'Da_Duyet' AND YEAR(hdn.thoiGianBatDau) = :nam")
    List<HoatDongNgoaiTruong> findHoatDongNgoaiTruongByGiangVienAndYear(@Param("maTaiKhoan") Long maTaiKhoan, @Param("nam") int nam);
    @Query("SELECT hdn FROM HoatDongNgoaiTruong hdn WHERE hdn.giangVien.maTaiKhoan = :maGiangVien AND hdn.trangThai = 'Da_Duyet' AND hdn.thoiGianBatDau BETWEEN :startDate AND :endDate")
    List<HoatDongNgoaiTruong> findHoatDongNgoaiTruongByGiangVienAndPeriod(@Param("maGiangVien") Long maGiangVien, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
