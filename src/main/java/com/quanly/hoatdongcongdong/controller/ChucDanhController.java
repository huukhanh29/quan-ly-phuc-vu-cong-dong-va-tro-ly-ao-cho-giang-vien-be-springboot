package com.quanly.hoatdongcongdong.controller;
import com.quanly.hoatdongcongdong.entity.*;
import com.quanly.hoatdongcongdong.exception.ResourceNotFoundException;
import com.quanly.hoatdongcongdong.payload.response.MessageResponse;
import com.quanly.hoatdongcongdong.repository.*;
import com.quanly.hoatdongcongdong.sercurity.services.TaiKhoanService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/chuc-danh")
@CrossOrigin(value = "*")
public class ChucDanhController {
    @Autowired
    private GiangVienRepository giangVienRepository;
    @Autowired
    private ChucDanhRepository chucDanhRepository;
    @Autowired
    private GioTichLuyRepository gioTichLuyRepository;
    @Autowired
    private TaiKhoanService taiKhoanService;
    @GetMapping("/lay-danh-sach")
    public ResponseEntity<?> getAllChucDanhs() {
        List<ChucDanh> chucDanhs = chucDanhRepository.findAll();
        return ResponseEntity.ok(chucDanhs);
    }

    @PutMapping("/cap-nhat/{maChucDanh}")
    public ResponseEntity<?> updateGiangVien( @PathVariable Long maChucDanh,
                                              HttpServletRequest httpServletRequest) {
        Long maTaiKhoan =  taiKhoanService.getCurrentUser(httpServletRequest).getMaTaiKhoan();
        GiangVien giangVien = giangVienRepository.findById(maTaiKhoan)
                .orElseThrow(() -> new ResourceNotFoundException("GiangVien", "maTaiKhoan", maTaiKhoan));

        if (giangVien.getChucDanh() != null) {
            if (maChucDanh.equals(giangVien.getChucDanh().getMaChucDanh())) {
                return new ResponseEntity<>(new MessageResponse("WARNING"), HttpStatus.OK);
            }
        }

        ChucDanh chucDanh = chucDanhRepository.findById(maChucDanh)
                .orElseThrow(() -> new ResourceNotFoundException("ChucDanh", "maChucDanh", maChucDanh));

        giangVien.setChucDanh(chucDanh);
        giangVienRepository.save(giangVien);

        return ResponseEntity.ok("Da cap nhat chuc danh");
    }
    @GetMapping("/du-lieu-bieu-do/{maTaiKhoan}/{academic}")
    public Map<String, Integer> getChartData(@PathVariable Long maTaiKhoan,
                                             @PathVariable String academic) {
        GiangVien giangVien = giangVienRepository.findById(maTaiKhoan)
                .orElseThrow(() -> new ResourceNotFoundException("GiangVien", "maTaiKhoan", maTaiKhoan));

        GioTichLuy gioTichLuy = gioTichLuyRepository.findByGiangVien_MaTaiKhoanAndNamHoc(giangVien.getMaTaiKhoan(), academic);
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
