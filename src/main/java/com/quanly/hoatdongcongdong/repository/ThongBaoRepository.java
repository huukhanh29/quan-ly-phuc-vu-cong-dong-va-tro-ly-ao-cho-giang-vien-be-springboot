package com.quanly.hoatdongcongdong.repository;

import com.quanly.hoatdongcongdong.entity.ThongBao;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ThongBaoRepository extends JpaRepository<ThongBao, Long> {
    List<ThongBao> findByTaiKhoan_MaTaiKhoanAndTrangThai(Long maTaiKhoan, ThongBao.TrangThai trangThai);

    @Query("SELECT tb FROM ThongBao tb WHERE tb.taiKhoan.maTaiKhoan = :maTk ORDER BY tb.maThongBao DESC")
    List<ThongBao> findByTaiKhoan_MaTaiKhoan(@Param("maTk") Long maTk);

    @Transactional
    void deleteAllByTaiKhoan_MaTaiKhoanAndTrangThai(Long maTk, ThongBao.TrangThai trangThai);
}
