package com.quanly.hoatdongcongdong.repository;

import com.quanly.hoatdongcongdong.entity.PhanHoi;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhanHoiRepository extends JpaRepository<PhanHoi, Long>, JpaSpecificationExecutor<PhanHoi> {
    PhanHoi findByNoiDung(String noiDung);

    Page<PhanHoi> findAll(Specification<PhanHoi> spec, Pageable pageable);

    PhanHoi findFirstByCauHoi_MaCauHoi(Long maCauHoi);

    @Transactional
    @Modifying
    @Query("DELETE FROM PhanHoi p WHERE p.cauHoi IS NOT NULL")
    void deleteByCauHoiNotNull();

    List<PhanHoi> findAllByCauHoiNotNull();
}
