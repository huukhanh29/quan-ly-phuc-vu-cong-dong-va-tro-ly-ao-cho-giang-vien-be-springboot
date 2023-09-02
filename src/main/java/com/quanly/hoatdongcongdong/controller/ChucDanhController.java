package com.quanly.hoatdongcongdong.controller;
import com.quanly.hoatdongcongdong.entity.*;
import com.quanly.hoatdongcongdong.exception.ResourceNotFoundException;
import com.quanly.hoatdongcongdong.payload.response.MessageResponse;
import com.quanly.hoatdongcongdong.repository.*;
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

        return ResponseEntity.ok("Da cap nhat chuc danh");
    }

    @GetMapping("/du-lieu-bieu-do/{academic}")
    public Map<String, Integer> getChartData(@PathVariable String academic,
                                             HttpServletRequest httpServletRequest) {
        GiangVien giangVien = giangVienService.findById(taiKhoanService.getCurrentUser(httpServletRequest).getMaTaiKhoan())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy giảng viên!"));

        GioTichLuy gioTichLuy = gioTichLuyService.findByGiangVien_MaTaiKhoanAndNamHoc(giangVien.getMaTaiKhoan(), academic);
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

}
