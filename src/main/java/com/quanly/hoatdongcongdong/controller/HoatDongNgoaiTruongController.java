package com.quanly.hoatdongcongdong.controller;

import com.quanly.hoatdongcongdong.entity.GiangVien;
import com.quanly.hoatdongcongdong.entity.HoatDongNgoaiTruong;
import com.quanly.hoatdongcongdong.payload.request.HoatDongNgoaiTruongRequest;
import com.quanly.hoatdongcongdong.payload.response.MessageResponse;
import com.quanly.hoatdongcongdong.repository.GiangVienRepository;
import com.quanly.hoatdongcongdong.repository.HoatDongNgoaiTruongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/hoat-dong-ngoai-truong")
public class HoatDongNgoaiTruongController {

    @Autowired
    private HoatDongNgoaiTruongRepository hoatDongNgoaiTruongRepository;
    @Autowired
    private GiangVienRepository giangVienRepository;

    @PostMapping("/them")
    public ResponseEntity<?> themHoatDongNgoaiTruong(@RequestBody HoatDongNgoaiTruongRequest hoatDongNgoaiTruongRequest) {
        Optional<GiangVien> giangVien = giangVienRepository.findById(hoatDongNgoaiTruongRequest.getMaGiangVien());
        if (giangVien.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Giảng viên không tồn tại"), HttpStatus.NOT_FOUND);
        }
        HoatDongNgoaiTruong hoatDongNgoaiTruong = new HoatDongNgoaiTruong();
        hoatDongNgoaiTruong.setGiangVien(giangVien.get());
        hoatDongNgoaiTruong.setTenHoatDong(hoatDongNgoaiTruongRequest.getTenHoatDong());
        hoatDongNgoaiTruong.setBanToChuc(hoatDongNgoaiTruongRequest.getBanToChuc());
        hoatDongNgoaiTruong.setMoTa(hoatDongNgoaiTruongRequest.getMoTa());
        hoatDongNgoaiTruong.setDiaDiem(hoatDongNgoaiTruongRequest.getDiaDiem());
        hoatDongNgoaiTruong.setGioTichLuyThamGia(hoatDongNgoaiTruongRequest.getGioTichLuyThamGia());
        hoatDongNgoaiTruong.setThoiGianBatDau(hoatDongNgoaiTruongRequest.getThoiGianBatDau());
        hoatDongNgoaiTruong.setThoiGianKetThuc(hoatDongNgoaiTruongRequest.getThoiGianKetThuc());
        hoatDongNgoaiTruong.setFileMinhChung(hoatDongNgoaiTruongRequest.getFileMinhChung());
        hoatDongNgoaiTruong.setTrangThai(hoatDongNgoaiTruongRequest.getTrangThai());

        hoatDongNgoaiTruongRepository.save(hoatDongNgoaiTruong);
        return ResponseEntity.ok(new MessageResponse("Thêm hoạt động ngoài trường thành công"));
    }

    @GetMapping("/lay-tat-ca")
    public ResponseEntity<List<HoatDongNgoaiTruong>> layTatCaHoatDongNgoaiTruong() {
        return ResponseEntity.ok(hoatDongNgoaiTruongRepository.findAll());
    }

    @GetMapping("/lay/{maHoatDongNgoaiTruong}")
    public ResponseEntity<?> layHoatDongNgoaiTruong(@PathVariable Long maHoatDongNgoaiTruong) {
        Optional<HoatDongNgoaiTruong> hoatDongNgoaiTruong = hoatDongNgoaiTruongRepository.findById(maHoatDongNgoaiTruong);
        if (hoatDongNgoaiTruong.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Hoạt động ngoài trường không tồn tại"), HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(hoatDongNgoaiTruong.get());
    }

    @PutMapping("/sua/{maHoatDongNgoaiTruong}")
    public ResponseEntity<?> suaHoatDongNgoaiTruong(@PathVariable Long maHoatDongNgoaiTruong, @RequestBody HoatDongNgoaiTruongRequest hoatDongNgoaiTruongRequest) {
        Optional<HoatDongNgoaiTruong> hoatDongNgoaiTruongOptional = hoatDongNgoaiTruongRepository.findById(maHoatDongNgoaiTruong);
        if (hoatDongNgoaiTruongOptional.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Hoạt động ngoài trường không tồn tại"), HttpStatus.NOT_FOUND);
        }

        Optional<GiangVien> giangVien = giangVienRepository.findById(hoatDongNgoaiTruongRequest.getMaGiangVien());
        if (giangVien.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Giảng viên không tồn tại"), HttpStatus.NOT_FOUND);
        }

        HoatDongNgoaiTruong hoatDongNgoaiTruong = hoatDongNgoaiTruongOptional.get();
        hoatDongNgoaiTruong.setGiangVien(giangVien.get());
        hoatDongNgoaiTruong.setTenHoatDong(hoatDongNgoaiTruongRequest.getTenHoatDong());
        hoatDongNgoaiTruong.setBanToChuc(hoatDongNgoaiTruongRequest.getBanToChuc());
        hoatDongNgoaiTruong.setMoTa(hoatDongNgoaiTruongRequest.getMoTa());
        hoatDongNgoaiTruong.setDiaDiem(hoatDongNgoaiTruongRequest.getDiaDiem());
        hoatDongNgoaiTruong.setGioTichLuyThamGia(hoatDongNgoaiTruongRequest.getGioTichLuyThamGia());
        hoatDongNgoaiTruong.setThoiGianBatDau(hoatDongNgoaiTruongRequest.getThoiGianBatDau());
        hoatDongNgoaiTruong.setThoiGianKetThuc(hoatDongNgoaiTruongRequest.getThoiGianKetThuc());
        hoatDongNgoaiTruong.setFileMinhChung(hoatDongNgoaiTruongRequest.getFileMinhChung());
        hoatDongNgoaiTruong.setTrangThai(hoatDongNgoaiTruongRequest.getTrangThai());

        hoatDongNgoaiTruongRepository.save(hoatDongNgoaiTruong);
        return ResponseEntity.ok(new MessageResponse("Cập nhật hoạt động ngoài trường thành công"));
    }

    @DeleteMapping("/xoa/{maHoatDongNgoaiTruong}")
    public ResponseEntity<?> xoaHoatDongNgoaiTruong(@PathVariable Long maHoatDongNgoaiTruong) {
        try {
            if (!hoatDongNgoaiTruongRepository.existsById(maHoatDongNgoaiTruong)) {
                return new ResponseEntity<>(new MessageResponse("Hoạt động ngoài trường không tồn tại"), HttpStatus.NOT_FOUND);
            }
            hoatDongNgoaiTruongRepository.deleteById(maHoatDongNgoaiTruong);
            return ResponseEntity.ok(new MessageResponse("Xóa hoạt động ngoài trường thành công"));
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("cant-delete"), HttpStatus.BAD_REQUEST);
        }
    }
}
