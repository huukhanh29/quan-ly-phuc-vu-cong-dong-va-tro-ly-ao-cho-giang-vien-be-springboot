package com.quanly.hoatdongcongdong.repository;

import com.quanly.hoatdongcongdong.entity.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface TaiKhoanRepository extends JpaRepository<TaiKhoan, Long>, JpaSpecificationExecutor<TaiKhoan> {
    Optional<TaiKhoan> findByTenDangNhap(String ten);
    Optional<TaiKhoan> findByMaTaiKhoan(Long ma);
    Boolean existsByTenDangNhap(String ten);
    Boolean existsByEmail(String email);

    Boolean existsByQuyen(TaiKhoan.Quyen quyen);
    List<TaiKhoan> findByQuyen(TaiKhoan.Quyen quyen);
}

