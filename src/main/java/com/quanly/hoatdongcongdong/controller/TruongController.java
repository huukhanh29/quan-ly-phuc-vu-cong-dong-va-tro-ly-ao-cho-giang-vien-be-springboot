//package com.quanly.hoatdongcongdong.controller;
//
//import com.quanly.hoatdongcongdong.entity.Truong;
//import com.quanly.hoatdongcongdong.payload.response.MessageResponse;
//import com.quanly.hoatdongcongdong.repository.TruongRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.Optional;
//
//@CrossOrigin(origins = "*")
//@RestController
//@RequestMapping("/api/truong")
//public class TruongController {
//
//    @Autowired
//    private TruongRepository truongRepository;
//
//    @PostMapping("/them")
//    public ResponseEntity<?> themTruong(@RequestBody Truong truongRequest) {
//        Truong truong = new Truong();
//        truong.setTenTruong(truongRequest.getTenTruong());
//
//        truongRepository.save(truong);
//        return ResponseEntity.ok(new MessageResponse("Thêm trường thành công"));
//    }
//
//    @GetMapping("/lay-tat-ca")
//    public ResponseEntity<List<Truong>> layTatCaTruong() {
//        return ResponseEntity.ok(truongRepository.findAll());
//    }
//
//    @GetMapping("/lay/{maTruong}")
//    public ResponseEntity<?> layTruong(@PathVariable Long maTruong) {
//        Optional<Truong> truong = truongRepository.findById(maTruong);
//        if (truong.isEmpty()) {
//            return new ResponseEntity<>(new MessageResponse("Trường không tồn tại"), HttpStatus.NOT_FOUND);
//        }
//        return ResponseEntity.ok(truong.get());
//    }
//
//    @PutMapping("/sua/{maTruong}")
//    public ResponseEntity<?> suaTruong(@PathVariable Long maTruong, @RequestBody Truong truongRequest) {
//        Optional<Truong> truongOptional = truongRepository.findById(maTruong);
//        if (truongOptional.isEmpty()) {
//            return new ResponseEntity<>(new MessageResponse("Trường không tồn tại"), HttpStatus.NOT_FOUND);
//        }
//
//        Truong truong = truongOptional.get();
//        truong.setTenTruong(truongRequest.getTenTruong());
//
//        truongRepository.save(truong);
//        return ResponseEntity.ok(new MessageResponse("Cập nhật trường thành công"));
//    }
//
//    @DeleteMapping("/xoa/{maTruong}")
//    public ResponseEntity<?> xoaTruong(@PathVariable Long maTruong) {
//        try {
//            if (!truongRepository.existsById(maTruong)) {
//                return new ResponseEntity<>(new MessageResponse("Trường không tồn tại"), HttpStatus.NOT_FOUND);
//            }
//            truongRepository.deleteById(maTruong);
//            return ResponseEntity.ok(new MessageResponse("Xóa trường thành công"));
//        } catch (Exception e) {
//            return new ResponseEntity<>(new MessageResponse("cant-delete"), HttpStatus.BAD_REQUEST);
//        }
//    }
//}
//
