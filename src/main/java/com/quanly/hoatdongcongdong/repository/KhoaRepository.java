package com.quanly.hoatdongcongdong.repository;

import com.quanly.hoatdongcongdong.entity.Khoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KhoaRepository extends JpaRepository<Khoa, Long> {
    List<Khoa> findByTruong_MaTruong(Long ma);
}
