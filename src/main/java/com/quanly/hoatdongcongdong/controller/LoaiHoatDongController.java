package com.quanly.hoatdongcongdong.controller;

import com.quanly.hoatdongcongdong.entity.LoaiHoatDong;
import com.quanly.hoatdongcongdong.payload.response.MessageResponse;
import com.quanly.hoatdongcongdong.repository.LoaiHoatDongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/loai-hoat-dong")
public class LoaiHoatDongController {

    @Autowired
    private LoaiHoatDongRepository loaiHoatDongRepository;

    @PostMapping("/them")
    public ResponseEntity<?> themLoaiHoatDong(@RequestBody LoaiHoatDong loaiHoatDongRequest) {

        if(loaiHoatDongRepository.existsByTenLoaiHoatDong(loaiHoatDongRequest.getTenLoaiHoatDong())){
            return new ResponseEntity<>(new MessageResponse("exist"), HttpStatus.OK);
        }
        LoaiHoatDong loaiHoatDong = new LoaiHoatDong();
        loaiHoatDong.setTenLoaiHoatDong(loaiHoatDongRequest.getTenLoaiHoatDong());
        loaiHoatDong.setMoTa(loaiHoatDongRequest.getMoTa());
        loaiHoatDongRepository.save(loaiHoatDong);
        return ResponseEntity.ok(new MessageResponse("Thêm loại hoạt động thành công"));
    }

    @GetMapping("/lay-tat-ca")
    public ResponseEntity<List<LoaiHoatDong>> layTatCaLoaiHoatDong() {
        return ResponseEntity.ok(loaiHoatDongRepository.findAll());
    }

    @GetMapping("/lay/{maLoaiHoatDong}")
    public ResponseEntity<?> layLoaiHoatDong(@PathVariable Long maLoaiHoatDong) {
        Optional<LoaiHoatDong> loaiHoatDong = loaiHoatDongRepository.findById(maLoaiHoatDong);
        if (loaiHoatDong.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Loại hoạt động không tồn tại"), HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(loaiHoatDong.get());
    }

    @PutMapping("/sua/{maLoaiHoatDong}")
    public ResponseEntity<?> suaLoaiHoatDong(@PathVariable Long maLoaiHoatDong, @RequestBody LoaiHoatDong loaiHoatDongRequest) {
        Optional<LoaiHoatDong> loaiHoatDongOptional = loaiHoatDongRepository.findById(maLoaiHoatDong);
        if (loaiHoatDongOptional.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("exist"), HttpStatus.OK);
        }
        if(loaiHoatDongRepository.existsByTenLoaiHoatDong(loaiHoatDongRequest.getTenLoaiHoatDong())){
            return new ResponseEntity<>(new MessageResponse("exist"), HttpStatus.OK);
        }
        LoaiHoatDong loaiHoatDong = loaiHoatDongOptional.get();
        loaiHoatDong.setTenLoaiHoatDong(loaiHoatDongRequest.getTenLoaiHoatDong());
        loaiHoatDong.setMoTa(loaiHoatDongRequest.getMoTa());

        loaiHoatDongRepository.save(loaiHoatDong);
        return ResponseEntity.ok(new MessageResponse("Cập nhật loại hoạt động thành công"));
    }

    @DeleteMapping("/xoa/{maLoaiHoatDong}")
    public ResponseEntity<?> xoaLoaiHoatDong(@PathVariable Long maLoaiHoatDong) {
        try {
            if (!loaiHoatDongRepository.existsById(maLoaiHoatDong)) {
                return new ResponseEntity<>(new MessageResponse("Loại hoạt động không tồn tại"), HttpStatus.NOT_FOUND);
            }
            loaiHoatDongRepository.deleteById(maLoaiHoatDong);
            return ResponseEntity.ok(new MessageResponse("Xóa loại hoạt động thành công"));
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("cant-delete"), HttpStatus.BAD_REQUEST);
        }
    }
}
