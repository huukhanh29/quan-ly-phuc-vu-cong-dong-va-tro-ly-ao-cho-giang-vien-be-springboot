package com.quanly.hoatdongcongdong.controller;
import com.quanly.hoatdongcongdong.entity.*;
import com.quanly.hoatdongcongdong.payload.response.MessageResponse;
import com.quanly.hoatdongcongdong.repository.ChucDanhRepository;
import com.quanly.hoatdongcongdong.service.ChucDanhService;
import com.quanly.hoatdongcongdong.service.GiangVienService;
import com.quanly.hoatdongcongdong.service.GioTichLuyService;
import com.quanly.hoatdongcongdong.service.TaiKhoanService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/chuc-danh")
@CrossOrigin(value = "*")
public class ChucDanhController {

    @Autowired
    private GiangVienService giangVienService;

    @Autowired
    private ChucDanhService chucDanhService;

    @Autowired
    private GioTichLuyService gioTichLuyService;
    @Autowired
    private ChucDanhRepository chucDanhRepository;

    @Autowired
    private TaiKhoanService taiKhoanService;

    @GetMapping("/lay-danh-sach")
    public ResponseEntity<?> getAllChucDanhs() {
        List<ChucDanh> chucDanhs = chucDanhService.findAll();
        return ResponseEntity.ok(chucDanhs);
    }

    @PutMapping("/cap-nhat/{maChucDanh}")
    public ResponseEntity<?> updateGiangVien(@PathVariable Long maChucDanh,
                                             HttpServletRequest httpServletRequest) {
        Long maTaiKhoan = taiKhoanService.getCurrentUser(httpServletRequest).getMaTaiKhoan();
        GiangVien giangVien = giangVienService.findById(maTaiKhoan)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy giảng viên!"));

        if (giangVien.getChucDanh() != null) {
            if (maChucDanh.equals(giangVien.getChucDanh().getMaChucDanh())) {
                return new ResponseEntity<>(new MessageResponse("WARNING"), HttpStatus.NOT_FOUND);
            }
        }

        Optional<ChucDanh> chucDanh = chucDanhService.findById(maChucDanh);
        if (chucDanh.isEmpty()) {
            throw new EntityNotFoundException("Không tìm thấy chức danh!");
        }
        giangVien.setChucDanh(chucDanh.get());
        giangVienService.saveGiangVien(giangVien);

        return ResponseEntity.ok(new MessageResponse("đã cập nhật"));
    }

    @GetMapping("/du-lieu-bieu-do/{academic}")
    public Map<String, Integer> getChartData(@PathVariable String academic,
                                             HttpServletRequest httpServletRequest) {
        GiangVien giangVien = giangVienService.findById(taiKhoanService.getCurrentUser(httpServletRequest).getMaTaiKhoan())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy giảng viên!"));

        GioTichLuy gioTichLuy = gioTichLuyService.findByGiangVien_MaTaiKhoanAndNam(giangVien.getMaTaiKhoan(), academic);
        int totalHours = 0;
        if (gioTichLuy != null) {
            totalHours = gioTichLuy.getTongSoGio();
        }
        ChucDanh chucDanh = giangVien.getChucDanh();
        int requiredHours = chucDanh.getGioBatBuoc();

        int missHours = requiredHours - totalHours;
        if (missHours < 0) {
            missHours = 0;
        }
        Map<String, Integer> chartData = new HashMap<>();
        chartData.put("missHours", missHours);
        chartData.put("totalHours", totalHours);
        chartData.put("requiredHours", requiredHours);
        return chartData;
    }
    @PostMapping("/them")
    public ResponseEntity<?> themChucDanh(@RequestBody ChucDanh chucDanhRequest) {
        ChucDanh chucDanh = new ChucDanh();
        chucDanh.setTenChucDanh(chucDanhRequest.getTenChucDanh());
        chucDanh.setGioBatBuoc(chucDanhRequest.getGioBatBuoc());

        chucDanhRepository.save(chucDanh);
        return ResponseEntity.ok(new MessageResponse("Thêm chức danh thành công"));
    }

    @GetMapping("/lay-tat-ca")
    public ResponseEntity<List<ChucDanh>> layTatCaChucDanh() {
        return ResponseEntity.ok(chucDanhRepository.findAll());
    }

    @GetMapping("/lay/{maChucDanh}")
    public ResponseEntity<?> layChucDanh(@PathVariable Long maChucDanh) {
        Optional<ChucDanh> chucDanh = chucDanhRepository.findById(maChucDanh);
        if (chucDanh.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Chức danh không tồn tại"), HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(chucDanh.get());
    }

    @PutMapping("/sua/{maChucDanh}")
    public ResponseEntity<?> suaChucDanh(@PathVariable Long maChucDanh, @RequestBody ChucDanh chucDanhRequest) {
        Optional<ChucDanh> chucDanhOptional = chucDanhRepository.findById(maChucDanh);
        if (chucDanhOptional.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Chức danh không tồn tại"), HttpStatus.NOT_FOUND);
        }

        ChucDanh chucDanh = chucDanhOptional.get();
        chucDanh.setTenChucDanh(chucDanhRequest.getTenChucDanh());
        chucDanh.setGioBatBuoc(chucDanhRequest.getGioBatBuoc());

        chucDanhRepository.save(chucDanh);
        return ResponseEntity.ok(new MessageResponse("Cập nhật chức danh thành công"));
    }

    @DeleteMapping("/xoa/{maChucDanh}")
    public ResponseEntity<?> xoaChucDanh(@PathVariable Long maChucDanh) {
        try {
            if (!chucDanhRepository.existsById(maChucDanh)) {
                return new ResponseEntity<>(new MessageResponse("Chức danh không tồn tại"), HttpStatus.NOT_FOUND);
            }
            chucDanhRepository.deleteById(maChucDanh);
            return ResponseEntity.ok(new MessageResponse("Xóa chức danh thành công"));
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("cant-delete"), HttpStatus.OK);
        }
    }
}
