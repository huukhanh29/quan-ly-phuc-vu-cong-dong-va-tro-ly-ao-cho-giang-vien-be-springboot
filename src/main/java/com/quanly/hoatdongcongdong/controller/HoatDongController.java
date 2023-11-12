package com.quanly.hoatdongcongdong.controller;

import com.quanly.hoatdongcongdong.entity.*;
import com.quanly.hoatdongcongdong.exception.ResourceNotFoundException;
import com.quanly.hoatdongcongdong.payload.response.HoatDongResponse;
import com.quanly.hoatdongcongdong.payload.response.HoatDongTongHopResponse;
import com.quanly.hoatdongcongdong.payload.response.MessageResponse;
import com.quanly.hoatdongcongdong.repository.FilesStorageService;
import com.quanly.hoatdongcongdong.repository.GiangVienRepository;
import com.quanly.hoatdongcongdong.repository.HoatDongRepository;
import com.quanly.hoatdongcongdong.repository.LoaiHoatDongRepository;
import com.quanly.hoatdongcongdong.sercurity.Helpers;
import com.quanly.hoatdongcongdong.service.*;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
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
    @Autowired
    private HoatDongRepository hoatDongRepository;
    @Autowired
    private FilesStorageService storageService;
    @Autowired
    private TaiKhoanService taiKhoanService;
    @Autowired
    private GiangVienRepository giangVienRepository;

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
        HoatDong hoatDong = hoatDongService.addHoatDong(hoatDongResponse);
        return ResponseEntity.ok(hoatDong);
    }

    @PutMapping("/sua-file/{maHoatDong}")
    public ResponseEntity<?> suaFileHoatDongNgoaiTruong(@PathVariable Long maHoatDong, @RequestParam("file") MultipartFile file) {
        Optional<HoatDong> hoatDongOptional = hoatDongRepository.findById(maHoatDong);
        if (hoatDongOptional.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Hoạt động không tồn tại"), HttpStatus.NOT_FOUND);
        }
        String newFileName = null;
        try {
            // Xóa tệp cũ (nếu có)
            String oldFile = hoatDongOptional.get().getFileQuyetDinh();
            if (oldFile != null) {
                storageService.delete(oldFile);
            }

            String originalFileName = Objects.requireNonNull(file.getOriginalFilename());
            String fileExtension = getFileExtension(originalFileName);
            String baseName = originalFileName.substring(0, originalFileName.lastIndexOf('.'));
            baseName = Helpers.createSlug(baseName);
            // Tạo một chuỗi timestamp
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            String timestamp = LocalDateTime.now().format(formatter);

            newFileName = timestamp + "_" + baseName + fileExtension;
            storageService.saveRandom(file, newFileName);

            // Cập nhật tên file mới
            HoatDong hoatDong = hoatDongOptional.get();
            hoatDong.setFileQuyetDinh(newFileName);
            hoatDongRepository.save(hoatDong);
            return ResponseEntity.ok(new MessageResponse("Cập nhật file cho hoạt động thành công"));
        } catch (Exception e) {
            // Xử lý lỗi nếu có
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageResponse("Could not update file for HoatDongNgoaiTruong. Error: " + e.getMessage()));
        }
    }

    @GetMapping("/{maHoatDong}/download")
    public ResponseEntity<?> downloadFile(@PathVariable Long maHoatDong) {
        Optional<HoatDong> hoatDong = hoatDongRepository.findById(maHoatDong);
        if (hoatDong.isPresent()) {
            try {
                String fileName = hoatDong.get().getFileQuyetDinh();
                if (fileName != null) {
                    Resource file = storageService.load(fileName); // Giả định storageService có một hàm load() để lấy file dựa trên tên

                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                            .body(file);
                } else {
                    return new ResponseEntity<>(new MessageResponse("Không có tệp được đính kèm cho hoạt động ngoại trường này"), HttpStatus.NOT_FOUND);
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(null);
            }
        } else {
            return new ResponseEntity<>(new MessageResponse("Hoạt động không tồn tại"), HttpStatus.NOT_FOUND);
        }
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex);
        }
        return "";
    }

    @GetMapping("/{maHoatDong}/ten-file")
    public ResponseEntity<?> getFileName(@PathVariable Long maHoatDong) {
        Optional<HoatDong> hoatDongNgoaiTruong = hoatDongRepository.findById(maHoatDong);
        if (hoatDongNgoaiTruong.isPresent()) {
            try {
                String fileName = hoatDongNgoaiTruong.get().getFileQuyetDinh();
                if (fileName != null) {
                    // Lấy tên tài liệu từ tên tệp
                    int underscoreIndex = fileName.indexOf("_");
                    if (underscoreIndex > 0 && underscoreIndex < fileName.length() - 1) {
                        String originalFileName = fileName.substring(underscoreIndex + 1);
                        return ResponseEntity.ok(originalFileName);
                    } else {
                        return new ResponseEntity<>(new MessageResponse("Không thể trích xuất tên tài liệu từ tên tệp"), HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                } else {
                    return new ResponseEntity<>(new MessageResponse("Không có tệp được đính kèm cho hoạt động ngoại trường này"), HttpStatus.NOT_FOUND);
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageResponse("Could not extract original filename. Error: " + e.getMessage()));
            }
        } else {
            return new ResponseEntity<>(new MessageResponse("Hoạt động ngoại trường không tồn tại"), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<HoatDong>> getAllHoatDongs() {
        return ResponseEntity.ok(hoatDongService.getAllHoatDongs());
    }

    @PutMapping("/cap-nhat/{maHoatDong}")
    public ResponseEntity<?> updateHoatDong(@PathVariable Long maHoatDong,
                                            @RequestBody HoatDongResponse hoatDongResponse) {
        // Process and update the hoatDong using HoatDongService
        HoatDong hoatDong = hoatDongService.updateHoatDong(maHoatDong, hoatDongResponse);
        return ResponseEntity.ok(hoatDong);
    }

    @DeleteMapping("/xoa/{maHoatDong}")
    public ResponseEntity<?> deleteHoatDong(@PathVariable Long maHoatDong) {
        if (dangKyHoatDongService.existsByHoatDong_MaHoatDong(maHoatDong)) {
            return new ResponseEntity<>(new MessageResponse("cant-delete"), HttpStatus.OK);
        }
        hoatDongService.deleteHoatDongById(maHoatDong);
        return ResponseEntity.ok(new MessageResponse("đã xóa"));
    }

    //năm học
    @GetMapping("/lay-danh-sach-nam1")
    public List<String> getYears() {
        return hoatDongService.getYears();
    }

    @GetMapping("/lay-danh-sach-nam")
    public List<Integer> getYears1() {
        return hoatDongService.getYears1();
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

    @GetMapping("/lay-hoat-dong-tong-hop")
    public ResponseEntity<?> getHoatDongByGiangVienAndYear(@RequestParam String nam,
                                                           @RequestParam Optional<Long> maGiangVien, HttpServletRequest httpServletRequest) {

        int number = Integer.parseInt(nam);
        TaiKhoan currentUser = taiKhoanService.getCurrentUser(httpServletRequest);
        Long maGiangVienToUse = maGiangVien.orElse(currentUser.getMaTaiKhoan());

        GiangVien giangVien = giangVienRepository.findById(maGiangVienToUse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "gv-notfound"));

        HoatDongTongHopResponse response = hoatDongService.getHoatDongInfo(giangVien.getMaTaiKhoan(), number);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
