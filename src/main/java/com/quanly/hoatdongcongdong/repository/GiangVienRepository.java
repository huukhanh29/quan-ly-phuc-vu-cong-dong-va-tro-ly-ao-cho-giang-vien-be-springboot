package com.quanly.hoatdongcongdong.repository;

import com.quanly.hoatdongcongdong.entity.GiangVien;
import com.quanly.hoatdongcongdong.entity.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface GiangVienRepository extends JpaRepository<GiangVien, Long> , JpaSpecificationExecutor<GiangVien> {
    List<GiangVien> findByMaTaiKhoanIn(List<Long> maTaiKhoanList);
}
