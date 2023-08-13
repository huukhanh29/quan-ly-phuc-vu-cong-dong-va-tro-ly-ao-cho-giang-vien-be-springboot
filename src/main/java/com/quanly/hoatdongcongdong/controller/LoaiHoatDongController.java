package com.quanly.hoatdongcongdong.controller;

import com.quanly.hoatdongcongdong.entity.LoaiHoatDong;
import com.quanly.hoatdongcongdong.repository.LoaiHoatDongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/loai-hoat-dong")
@CrossOrigin(value = "*")
public class LoaiHoatDongController {
    @Autowired
    private LoaiHoatDongRepository loaiHoatDongRepository;
    @GetMapping("/lay-danh-sach")
    public ResponseEntity<?> getAllLoaiHoatDong() {
        List<LoaiHoatDong> loaiHoatDongs = loaiHoatDongRepository.findAll();
        return ResponseEntity.ok(loaiHoatDongs);
    }
}
