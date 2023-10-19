package com.quanly.hoatdongcongdong.repository;

import com.quanly.hoatdongcongdong.entity.DangKyHoatDong;
import com.quanly.hoatdongcongdong.entity.HoatDong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DangKyHoatDongRepository extends JpaRepository<DangKyHoatDong, Long>, JpaSpecificationExecutor<DangKyHoatDong> {
    List<DangKyHoatDong> findByGiangVien_MaTaiKhoan(Long maTk);
    boolean existsByHoatDong_MaHoatDong(Long maHoatDong);
    DangKyHoatDong findByGiangVien_TaiKhoan_TenDangNhapAndHoatDong_MaHoatDong(String ten, Long ma);
    boolean existsByGiangVien_MaTaiKhoanAndHoatDong_MaHoatDong(Long maTaiKhoan, Long maHoatDong);
    boolean existsByGiangVien_TaiKhoan_TenDangNhapAndHoatDong_MaHoatDong(String ten, Long ma);
    @Query("SELECT dk.hoatDong FROM DangKyHoatDong dk WHERE dk.giangVien.taiKhoan.tenDangNhap = :ten")
    List<HoatDong> findHoatDongsByGiangVien(@Param("ten") String ten);
}
