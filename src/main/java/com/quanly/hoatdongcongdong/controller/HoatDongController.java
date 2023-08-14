package com.quanly.hoatdongcongdong.controller;

import com.quanly.hoatdongcongdong.entity.HoatDong;
import com.quanly.hoatdongcongdong.repository.HoatDongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hoat-dong")
@CrossOrigin(value = "*")
public class HoatDongController {
    @Autowired
    private HoatDongRepository hoatDongRepository;
    @GetMapping("/{maHoatDong}")
    public ResponseEntity<HoatDong> getHoatDongById(@PathVariable Long maHoatDong) {
        // Tìm hoạt động theo mã hoạt động
        HoatDong hoatDong = hoatDongRepository.findById(maHoatDong)
                .orElse(null);

        if (hoatDong == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(hoatDong);
    }
}
