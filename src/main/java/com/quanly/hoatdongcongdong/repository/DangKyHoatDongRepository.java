package com.quanly.hoatdongcongdong.repository;

import com.quanly.hoatdongcongdong.entity.DangKyHoatDong;
import com.quanly.hoatdongcongdong.entity.GiangVien;
import com.quanly.hoatdongcongdong.entity.HoatDong;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DangKyHoatDongRepository extends JpaRepository<DangKyHoatDong, Long>, JpaSpecificationExecutor<DangKyHoatDong> {
    List<DangKyHoatDong> findByGiangVien_MaTaiKhoan(Long maTk);
    List<DangKyHoatDong> findByHoatDong_MaHoatDong(Long maHd);
    boolean existsByHoatDong_MaHoatDong(Long maHoatDong);
    DangKyHoatDong findByGiangVien_TaiKhoan_TenDangNhapAndHoatDong_MaHoatDong(String ten, Long ma);
    boolean existsByGiangVien_MaTaiKhoanAndHoatDong_MaHoatDong(Long maTaiKhoan, Long maHoatDong);
    boolean existsByGiangVien_TaiKhoan_TenDangNhapAndHoatDong_MaHoatDong(String ten, Long ma);
    @Query("SELECT dk.hoatDong FROM DangKyHoatDong dk WHERE dk.giangVien.taiKhoan.tenDangNhap = :ten")
    List<HoatDong> findHoatDongsByGiangVien(@Param("ten") String ten);
    @Query("SELECT d.giangVien FROM DangKyHoatDong d WHERE d.hoatDong.maHoatDong = :maHoatDong")
    List<GiangVien> findGiangViensByHoatDong(@Param("maHoatDong") Long maHoatDong);
    @Query("SELECT dk.hoatDong FROM DangKyHoatDong dk WHERE dk.giangVien.maTaiKhoan = :maTaiKhoan AND dk.trangThaiDangKy = 'Da_Duyet' AND YEAR(dk.hoatDong.thoiGianBatDau) = :nam")
    List<HoatDong> findHoatDongByGiangVienAndYear(@Param("maTaiKhoan") Long maTaiKhoan, @Param("nam") int nam);
    @Query("SELECT d.hoatDong FROM DangKyHoatDong d WHERE d.trangThaiDangKy = 'Chua_Duyet' AND d.hoatDong.trangThaiHoatDong = 'DA_DIEN_RA'")
    List<HoatDong> findHoatDongByDangKyChuaDuyetVaDaDienRa();
    @Modifying
    @Transactional
    @Query("DELETE FROM DangKyHoatDong d WHERE d.hoatDong.maHoatDong = :maHoatDong AND d.trangThaiDangKy = :trangThai")
    void deleteByHoatDongAndTrangThaiDangKy(Long maHoatDong, DangKyHoatDong.TrangThaiDangKy trangThai);
}
