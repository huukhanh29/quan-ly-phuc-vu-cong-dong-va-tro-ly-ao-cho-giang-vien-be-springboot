package com.quanly.hoatdongcongdong.repository;
import com.quanly.hoatdongcongdong.entity.LichSu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.*;
@Repository
public interface LichSuRepository extends JpaRepository<LichSu, Long>, JpaSpecificationExecutor<LichSu> {
    List<LichSu> findBySinhVien_MaTaiKhoan(Long maTaiKhoan);

    Page<LichSu> findAll(Specification<LichSu> spec, Pageable pageable);

    @Query("SELECT h FROM LichSu h WHERE h.ngayTao BETWEEN :startDate AND :endDate")
    List<LichSu> findByNgayTaoBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT DISTINCT YEAR(h.ngayTao) FROM LichSu h ORDER BY YEAR(h.ngayTao) DESC")
    List<Integer> findDistinctNam();

    LichSu findFirstByCauHoi_MaCauHoi(Long maCauHoi);
}
