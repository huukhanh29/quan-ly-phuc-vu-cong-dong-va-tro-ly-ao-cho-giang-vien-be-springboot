package com.quanly.hoatdongcongdong.repository;
import com.quanly.hoatdongcongdong.entity.CauHoi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CauHoiRepository extends JpaRepository<CauHoi, Long>, JpaSpecificationExecutor<CauHoi> {
    CauHoi findByCauHoi(String cauHoi);

    List<CauHoi> findAll();

    // Tìm kiếm phần tử có maCauHoi lớn nhất
    CauHoi findTopByOrderByMaCauHoiDesc();

    Page<CauHoi> findAll(Specification<CauHoi> spec, Pageable pageable);

    @Query("SELECT c FROM CauHoi c WHERE c.maCauHoi = :maCauHoi")
    CauHoi fetchByMaCauHoi(@Param("maCauHoi") Long maCauHoi);

}
