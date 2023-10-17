package com.quanly.hoatdongcongdong.repository;

import com.quanly.hoatdongcongdong.entity.GiangVien;
import com.quanly.hoatdongcongdong.entity.GioTichLuy;
import com.quanly.hoatdongcongdong.entity.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GioTichLuyRepository extends JpaRepository<GioTichLuy, Long> {
    GioTichLuy findByGiangVien_MaTaiKhoan(Long maTk);
    List<GioTichLuy> findByNam(String namHoc);
    GioTichLuy findByGiangVien_MaTaiKhoanAndNam(Long nguoiDungId, String nam);

    @Query("SELECT DISTINCT gio.nam FROM GioTichLuy gio WHERE gio.giangVien.taiKhoan.maTaiKhoan = :maTk ORDER BY gio.nam asc ")
    List<String> findDistinctNamByGiangVien(@Param("maTk") Long maTk);
    GioTichLuy findByNamAndGiangVien_MaTaiKhoan(String nam, Long maTk);
}
