package com.quanly.hoatdongcongdong.repository;

import com.quanly.hoatdongcongdong.entity.HoatDong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface HoatDongRepository extends JpaRepository<HoatDong, Long> {

}

