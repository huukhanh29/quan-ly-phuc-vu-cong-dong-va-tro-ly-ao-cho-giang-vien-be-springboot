package com.quanly.hoatdongcongdong.repository;

import com.quanly.hoatdongcongdong.entity.GiangVien;
import com.quanly.hoatdongcongdong.entity.HoatDong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface HoatDongRepository extends JpaRepository<HoatDong, Long>, JpaSpecificationExecutor<HoatDong> {
    @Query("SELECT DISTINCT YEAR(a.thoiGianBatDau) FROM HoatDong a ORDER BY YEAR(a.thoiGianBatDau) ASC")
    List<Integer> findYears();
    Long countByTrangThaiHoatDong(HoatDong.TrangThaiHoatDong trangThaiHoatDong);
    @Query("SELECT hd FROM HoatDong hd JOIN hd.giangVienToChucs gv WHERE gv.maTaiKhoan = :maTaiKhoan AND hd.trangThaiHoatDong = 'DA_DIEN_RA' AND YEAR(hd.thoiGianBatDau) = :nam")
    List<HoatDong> findHoatDongTocChucByGiangVienAndYear(@Param("maTaiKhoan") Long maTaiKhoan, @Param("nam") int nam);
    @Query("SELECT hd FROM HoatDong hd JOIN hd.giangVienToChucs gv WHERE gv.maTaiKhoan = :maGiangVien AND hd.trangThaiHoatDong = 'DA_DIEN_RA' AND hd.thoiGianBatDau BETWEEN :startDate AND :endDate")
    List<HoatDong> findHoatDongTocChucByGiangVienAndPeriod(@Param("maGiangVien") Long maGiangVien, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    @Query("SELECT hd FROM DangKyHoatDong dk JOIN dk.hoatDong hd WHERE dk.giangVien.maTaiKhoan = :maGiangVien AND dk.trangThaiDangKy = 'Da_Duyet' AND hd.thoiGianBatDau BETWEEN :startDate AND :endDate")
    List<HoatDong> findHoatDongByGiangVienAndPeriod(@Param("maGiangVien") Long maGiangVien, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}

