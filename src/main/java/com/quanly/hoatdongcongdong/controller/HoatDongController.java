package com.quanly.hoatdongcongdong.controller;

import com.quanly.hoatdongcongdong.entity.HoatDong;
import com.quanly.hoatdongcongdong.entity.LoaiHoatDong;
import com.quanly.hoatdongcongdong.exception.ResourceNotFoundException;
import com.quanly.hoatdongcongdong.payload.response.HoatDongResponse;
import com.quanly.hoatdongcongdong.payload.response.MessageResponse;
import com.quanly.hoatdongcongdong.repository.LoaiHoatDongRepository;
import com.quanly.hoatdongcongdong.service.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/hoat-dong")
@CrossOrigin(value = "*")
public class HoatDongController {
    @Autowired
    private HoatDongService hoatDongService;
    @Autowired
    private LoaiHoatDongRepository loaiHoatDongRepository;
    @Autowired
    private DangKyHoatDongService dangKyHoatDongService;

    @GetMapping("/{maHoatDong}")
    public ResponseEntity<?> getHoatDongById(@PathVariable Long maHoatDong) {
        Optional<HoatDong> hoatDong = hoatDongService.findById(maHoatDong);

        if (hoatDong.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Không tìm thấy hoạt động!"), HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(hoatDong);
    }

    @GetMapping("/lay-danh-sach")
    public Page<HoatDong> getAllHoatDong(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ngayTao") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir,
            @RequestParam(required = false, defaultValue = "") String searchTerm,
            @RequestParam(required = false, defaultValue = "") String type,
            @RequestParam(required = false) HoatDong.TrangThaiHoatDong status,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime
    ) {
        return hoatDongService.getAllHoatDong(page, size, sortBy, sortDir, searchTerm, type, status, startTime, endTime);
    }

    @PostMapping("/them-moi")
    public ResponseEntity<?> addHoatDong(@RequestBody HoatDongResponse hoatDongResponse) {
        hoatDongService.addHoatDong(hoatDongResponse);
        return ResponseEntity.ok(new MessageResponse("đã thêm"));
    }
    @GetMapping("/all")
    public ResponseEntity<List<HoatDong>> getAllHoatDongs() {
        return ResponseEntity.ok(hoatDongService.getAllHoatDongs());
    }
    @PutMapping("/cap-nhat/{maHoatDong}")
    public ResponseEntity<?> updateHoatDong(@PathVariable Long maHoatDong,
                                                 @RequestBody HoatDongResponse hoatDongResponse) {
        // Process and update the hoatDong using HoatDongService
        hoatDongService.updateHoatDong(maHoatDong, hoatDongResponse);
        return ResponseEntity.ok(new MessageResponse("đã cập nhật"));
    }

    @DeleteMapping("/xoa/{maHoatDong}")
    public ResponseEntity<?> deleteHoatDong(@PathVariable Long maHoatDong) {
        if (dangKyHoatDongService.existsByHoatDong_MaHoatDong(maHoatDong)) {
            return new ResponseEntity<>(new MessageResponse("cant-delete"), HttpStatus.BAD_REQUEST);
        }
        hoatDongService.deleteHoatDongById(maHoatDong);
        return ResponseEntity.ok(new MessageResponse("đã xóa"));

    }

    @GetMapping("/lay-danh-sach-nam")
    public List<String> getYears() {
        return hoatDongService.getYears();
    }

    @GetMapping("/so-hoat-dong-chua-dien-ra")
    public Long countUpcomingActivities() {
        return hoatDongService.countUpcomingActivities();
    }

    @GetMapping("/danh-sach-loai-hoat-dong")
    public ResponseEntity<List<LoaiHoatDong>> getAllLoaiHoatDong() {
        List<LoaiHoatDong> loaiHoatDongs = loaiHoatDongRepository.findAll();
        return ResponseEntity.ok(loaiHoatDongs);
    }
}
