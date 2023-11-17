package com.quanly.hoatdongcongdong.controller;


import com.quanly.hoatdongcongdong.entity.Khoa;
import com.quanly.hoatdongcongdong.payload.request.KhoaRequest;
import com.quanly.hoatdongcongdong.payload.response.MessageResponse;
import com.quanly.hoatdongcongdong.repository.KhoaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/khoa")
public class KhoaController {

    @Autowired
    private KhoaRepository khoaRepository;
//    @Autowired
//    private TruongRepository truongRepository;

    @PostMapping("/them")
    public ResponseEntity<?> themKhoa(@RequestBody KhoaRequest khoaRequest) {
//        Optional<Truong> truong = truongRepository.findById(khoaRequest.getMaTruong());
//        if (truong.isEmpty()) {
//            Khoa khoa = new Khoa();
//            khoa.setTenKhoa(khoaRequest.getTenKhoa());
//            khoaRepository.save(khoa);
//            return ResponseEntity.ok(new MessageResponse("Thêm khoa không có trường thành công"));
//        }else {
//            Khoa khoa = new Khoa();
//            khoa.setTruong(truong.get());
//            khoa.setTenKhoa(khoaRequest.getTenKhoa());
//            khoaRepository.save(khoa);
//            return ResponseEntity.ok(new MessageResponse("Thêm khoa thành công"));
//        }
        Khoa khoa = new Khoa();
        khoa.setTenKhoa(khoaRequest.getTenKhoa());
        khoaRepository.save(khoa);
        return ResponseEntity.ok(new MessageResponse("Thêm khoa thành công"));

    }

    @GetMapping("/lay-tat-ca")
    public ResponseEntity<List<Khoa>> layTatCaKhoa() {
        return ResponseEntity.ok(khoaRepository.findAll());
    }

    @GetMapping("/lay/{maKhoa}")
    public ResponseEntity<?> layKhoa(@PathVariable Long maKhoa) {
        Optional<Khoa> khoa = khoaRepository.findById(maKhoa);
        if (khoa.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Khoa không tồn tại"), HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(khoa.get());
    }

    @PutMapping("/sua/{maKhoa}")
    public ResponseEntity<?> suaKhoa(@PathVariable Long maKhoa, @RequestBody KhoaRequest khoaRequest) {
        Optional<Khoa> khoaOptional = khoaRepository.findById(maKhoa);
        if (khoaOptional.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Khoa không tồn tại"), HttpStatus.NOT_FOUND);
        }

//        Optional<Truong> truong = truongRepository.findById(khoaRequest.getMaTruong());
//        if (truong.isEmpty()) {
//            return new ResponseEntity<>(new MessageResponse("Trường không tồn tại"), HttpStatus.NOT_FOUND);
//        }

        Khoa khoa = khoaOptional.get();
       // khoa.setTruong(truong.get());
        khoa.setTenKhoa(khoaRequest.getTenKhoa());

        khoaRepository.save(khoa);
        return ResponseEntity.ok(new MessageResponse("Cập nhật khoa thành công"));
    }

//    @GetMapping("/lay-theo-truong/{maTruong}")
//    public ResponseEntity<?> layKhoaTheoTruong(@PathVariable Long maTruong) {
//        List<Khoa> khoaTheoTruong = khoaRepository.findByTruong_MaTruong(maTruong);
//        return ResponseEntity.ok(khoaTheoTruong);
//    }

    @DeleteMapping("/xoa/{maKhoa}")
    public ResponseEntity<?> xoaKhoa(@PathVariable Long maKhoa) {
        try {
            if (!khoaRepository.existsById(maKhoa)) {
                return new ResponseEntity<>(new MessageResponse("Khoa không tồn tại"), HttpStatus.NOT_FOUND);
            }
            khoaRepository.deleteById(maKhoa);
            return ResponseEntity.ok(new MessageResponse("Xóa khoa thành công"));
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("cant-delete"), HttpStatus.BAD_REQUEST);
        }
    }
}
