package com.quanly.hoatdongcongdong.controller;

import com.quanly.hoatdongcongdong.entity.*;
import com.quanly.hoatdongcongdong.payload.request.HoatDongNgoaiTruongRequest;
import com.quanly.hoatdongcongdong.payload.response.MessageResponse;
import com.quanly.hoatdongcongdong.repository.FilesStorageService;
import com.quanly.hoatdongcongdong.repository.GiangVienRepository;
import com.quanly.hoatdongcongdong.repository.GioTichLuyRepository;
import com.quanly.hoatdongcongdong.repository.HoatDongNgoaiTruongRepository;
import com.quanly.hoatdongcongdong.sercurity.Helpers;
import com.quanly.hoatdongcongdong.service.ThongBaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/hoat-dong-ngoai-truong")
public class HoatDongNgoaiTruongController {

    @Autowired
    private HoatDongNgoaiTruongRepository hoatDongNgoaiTruongRepository;

    @Autowired
    private GiangVienRepository giangVienRepository;
    @Autowired
    private FilesStorageService storageService;
    @Autowired
    private ThongBaoService thongBaoService;
    @Autowired
    private GioTichLuyRepository gioTichLuyRepository;
    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @PostMapping("/them")
    public ResponseEntity<?> themHoatDongNgoaiTruong(
            @RequestBody HoatDongNgoaiTruongRequest hoatDongNgoaiTruongRequest) {
        try {
            GiangVien giangVien = giangVienRepository.findByTaiKhoan_TenDangNhap(hoatDongNgoaiTruongRequest.getTenDangNhap());
            if (giangVien == null) {
                return new ResponseEntity<>(new MessageResponse("Giảng viên không tồn tại"), HttpStatus.NOT_FOUND);
            }
            HoatDongNgoaiTruong hoatDongNgoaiTruong = new HoatDongNgoaiTruong();
            hoatDongNgoaiTruong.setGiangVien(giangVien);
            hoatDongNgoaiTruong.setTenHoatDong(hoatDongNgoaiTruongRequest.getTenHoatDong());
            hoatDongNgoaiTruong.setBanToChuc(hoatDongNgoaiTruongRequest.getBanToChuc());
            hoatDongNgoaiTruong.setMoTa(hoatDongNgoaiTruongRequest.getMoTa());
            hoatDongNgoaiTruong.setDiaDiem(hoatDongNgoaiTruongRequest.getDiaDiem());
            hoatDongNgoaiTruong.setThoiGianBatDau(hoatDongNgoaiTruongRequest.getThoiGianBatDau());
            hoatDongNgoaiTruong.setThoiGianKetThuc(hoatDongNgoaiTruongRequest.getThoiGianKetThuc());
            hoatDongNgoaiTruong.setTrangThai(HoatDongNgoaiTruong.TrangThai.Chua_Duyet);

            hoatDongNgoaiTruongRepository.save(hoatDongNgoaiTruong);
            return ResponseEntity.ok(hoatDongNgoaiTruong);
        } catch (Exception e) {
            // Xử lý lỗi nếu có
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageResponse("Could not create HoatDongNgoaiTruong. Error: " + e.getMessage()));
        }
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex);
        }
        return "";
    }

    @PutMapping("/sua/{maHoatDongNgoaiTruong}")
    public ResponseEntity<?> suaHoatDongNgoaiTruong(@PathVariable Long maHoatDongNgoaiTruong, @RequestBody HoatDongNgoaiTruongRequest hoatDongNgoaiTruongRequest) {
        Optional<HoatDongNgoaiTruong> hoatDongNgoaiTruongOptional = hoatDongNgoaiTruongRepository.findById(maHoatDongNgoaiTruong);
        if (hoatDongNgoaiTruongOptional.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Hoạt động ngoài trường không tồn tại"), HttpStatus.NOT_FOUND);
        }

        HoatDongNgoaiTruong hoatDongNgoaiTruong = hoatDongNgoaiTruongOptional.get();
        hoatDongNgoaiTruong.setTenHoatDong(hoatDongNgoaiTruongRequest.getTenHoatDong());
        hoatDongNgoaiTruong.setBanToChuc(hoatDongNgoaiTruongRequest.getBanToChuc());
        hoatDongNgoaiTruong.setMoTa(hoatDongNgoaiTruongRequest.getMoTa());
        hoatDongNgoaiTruong.setDiaDiem(hoatDongNgoaiTruongRequest.getDiaDiem());
        hoatDongNgoaiTruong.setThoiGianBatDau(hoatDongNgoaiTruongRequest.getThoiGianBatDau());
        hoatDongNgoaiTruong.setThoiGianKetThuc(hoatDongNgoaiTruongRequest.getThoiGianKetThuc());

        hoatDongNgoaiTruongRepository.save(hoatDongNgoaiTruong);
        return ResponseEntity.ok(new MessageResponse("Cập nhật hoạt động ngoài trường thành công"));
    }
    @PutMapping("/sua-file/{maHoatDongNgoaiTruong}")
    public ResponseEntity<?> suaFileHoatDongNgoaiTruong(@PathVariable Long maHoatDongNgoaiTruong, @RequestParam("file") MultipartFile file) {
        Optional<HoatDongNgoaiTruong> hoatDongNgoaiTruongOptional = hoatDongNgoaiTruongRepository.findById(maHoatDongNgoaiTruong);
        if (hoatDongNgoaiTruongOptional.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Hoạt động ngoài trường không tồn tại"), HttpStatus.NOT_FOUND);
        }

        String newFileName = null;
        try {
            // Xóa tệp cũ (nếu có)
            String oldFile = hoatDongNgoaiTruongOptional.get().getFileMinhChung();
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
            HoatDongNgoaiTruong hoatDongNgoaiTruong = hoatDongNgoaiTruongOptional.get();
            hoatDongNgoaiTruong.setFileMinhChung(newFileName);

            hoatDongNgoaiTruongRepository.save(hoatDongNgoaiTruong);
            return ResponseEntity.ok(hoatDongNgoaiTruong);
        } catch (Exception e) {
            // Xử lý lỗi nếu có
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageResponse("Could not update file for HoatDongNgoaiTruong. Error: " + e.getMessage()));
        }
    }

    @Transactional
    @PutMapping("/phe-duyet/{maHoatDongNgoaiTruong}")
    public ResponseEntity<?> pheDuyetHoatDongNgoaiTruong(@PathVariable Long maHoatDongNgoaiTruong,
                                                         @RequestBody HoatDongNgoaiTruongRequest hoatDongNgoaiTruongRequest) {
        Optional<HoatDongNgoaiTruong> hoatDongNgoaiTruongOptional = hoatDongNgoaiTruongRepository.findById(maHoatDongNgoaiTruong);
        if (hoatDongNgoaiTruongOptional.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Hoạt động ngoài trường không tồn tại"), HttpStatus.NOT_FOUND);
        }
        if (hoatDongNgoaiTruongOptional.get().getTrangThai() == HoatDongNgoaiTruong.TrangThai.Da_Duyet) {
            return new ResponseEntity<>(new MessageResponse("daduyet"), HttpStatus.OK);
        }
        messagingTemplate.convertAndSendToUser(hoatDongNgoaiTruongOptional.get().getGiangVien().getTaiKhoan().getTenDangNhap(), "/queue/messages", "approve-activity");
        HoatDongNgoaiTruong hoatDongNgoaiTruong = hoatDongNgoaiTruongOptional.get();
        hoatDongNgoaiTruong.setGioTichLuyThamGia(hoatDongNgoaiTruongRequest.getGioTichLuyThamGia());
        hoatDongNgoaiTruong.setTrangThai(HoatDongNgoaiTruong.TrangThai.Da_Duyet);
        hoatDongNgoaiTruongRepository.save(hoatDongNgoaiTruong);
        String nam = String.valueOf(hoatDongNgoaiTruong.getThoiGianBatDau().getYear());
        int gioTichLuyThamGia = hoatDongNgoaiTruong.getGioTichLuyThamGia();
        GioTichLuy gioTichLuy = gioTichLuyRepository.findByGiangVien_MaTaiKhoanAndNam(hoatDongNgoaiTruong.getGiangVien().getMaTaiKhoan(), nam);

        LocalDateTime thoiGianBatDau = hoatDongNgoaiTruong.getThoiGianBatDau();
        String namHoc = String.valueOf(thoiGianBatDau.getYear());
        if (gioTichLuy == null) {
            gioTichLuy = new GioTichLuy();
            gioTichLuy.setGiangVien(hoatDongNgoaiTruong.getGiangVien());
            gioTichLuy.setTongSoGio(gioTichLuyThamGia);
            gioTichLuy.setNam(namHoc);
            System.out.println(gioTichLuy.getNam());
        } else {
            System.out.println(gioTichLuy.getNam());
            gioTichLuy.setTongSoGio(gioTichLuy.getTongSoGio() + gioTichLuyThamGia);
        }
        gioTichLuyRepository.save(gioTichLuy);

        String tieuDe = "Duyệt tham gia hoạt động ngoài trường";
        String nDung = "Yêu cầu duyệt tham gia hoạt động " +
                hoatDongNgoaiTruong.getTenHoatDong() + " của bạn đã được duyệt.";
        ThongBao thongBao = thongBaoService.taoMoiThongBao(
                hoatDongNgoaiTruong.getGiangVien().getTaiKhoan(),
                tieuDe, nDung, ThongBao.TrangThai.ChuaDoc
        );
        thongBaoService.luuThongBao(thongBao);
        return ResponseEntity.ok(new MessageResponse("Duyệt hoạt động ngoài trường thành công"));

    }

    @GetMapping("/lay-tat-ca")
    public ResponseEntity<?> layTatCaHoatDongNgoaiTruong(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ngayDangKy") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir,
            @RequestParam(required = false, defaultValue = "") String searchTerm,
            @RequestParam(required = false) String trangThai,
            @RequestParam(required = false) String tenDangNhap,
            @RequestParam(required = false) String year
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable paging = PageRequest.of(page, size, sort);

        Specification<HoatDongNgoaiTruong> spec = (root, criteriaQuery, criteriaBuilder) -> {
            if (!searchTerm.isEmpty()) {
                String pattern = "%" + searchTerm + "%";
                // Thay đổi tùy thuộc vào trường dữ liệu bạn muốn tìm kiếm
                return criteriaBuilder.like(root.get("tenHoatDong"), pattern);
            } else {
                return criteriaBuilder.isTrue(criteriaBuilder.literal(true)); // always true
            }
        };

        if (trangThai != null) {
            spec = spec.and((root, criteriaQuery, criteriaBuilder) -> {
                return criteriaBuilder.equal(root.get("trangThai"), HoatDongNgoaiTruong.TrangThai.valueOf(trangThai));          });
        }
        if (tenDangNhap != null) {
            spec = spec.and((root, criteriaQuery, criteriaBuilder) -> {
                return criteriaBuilder.like(root.get("giangVien").get("taiKhoan").get("tenDangNhap"), "%" + tenDangNhap + "%");
            });
        }
        if (year != null) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                LocalDateTime startOfYear = LocalDateTime.of(Integer.parseInt(year), 1, 1, 0, 0, 0);
                LocalDateTime endOfYear = LocalDateTime.of(Integer.parseInt(year), 12, 31, 23, 59, 59);
                return criteriaBuilder.between(root.get("thoiGianBatDau"), startOfYear, endOfYear);
            });
        }
        Page<HoatDongNgoaiTruong> hoatDongPage = hoatDongNgoaiTruongRepository.findAll(spec, paging);
        return ResponseEntity.ok(hoatDongPage);
    }


    @GetMapping("/lay/{maHoatDongNgoaiTruong}")
    public ResponseEntity<?> layHoatDongNgoaiTruong(@PathVariable Long maHoatDongNgoaiTruong) {
        Optional<HoatDongNgoaiTruong> hoatDongNgoaiTruong = hoatDongNgoaiTruongRepository.findById(maHoatDongNgoaiTruong);
        if (hoatDongNgoaiTruong.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("Hoạt động ngoài trường không tồn tại"), HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(hoatDongNgoaiTruong.get());
    }
    @Transactional
    @DeleteMapping("/huy/{maHoatDongNgoaiTruong}")
    public ResponseEntity<?> huyHoatDongNgoaiTruong(@PathVariable Long maHoatDongNgoaiTruong) {
        try {
            Optional<HoatDongNgoaiTruong> hoatDongNgoaiTruongOptional = hoatDongNgoaiTruongRepository.findById(maHoatDongNgoaiTruong);
            if (hoatDongNgoaiTruongOptional.isEmpty()) {
                return new ResponseEntity<>(new MessageResponse("Hoạt động ngoài trường không tồn tại"), HttpStatus.NOT_FOUND);
            }
            messagingTemplate.convertAndSendToUser(hoatDongNgoaiTruongOptional.get().getGiangVien().getTaiKhoan().getTenDangNhap(), "/queue/messages", "destroy-activity");

            String tieuDe = "Hủy đăng ký hoạt động ngoài trường";
            String nDung = "Xác nhận tham gia hoạt động " +
                    hoatDongNgoaiTruongOptional.get().getTenHoatDong() +
                    " của bạn đã bị hủy. Vui lòng kiểm tra minh chứng và gửi lại yêu cầu!";
            ThongBao thongBao = thongBaoService.taoMoiThongBao(
                    hoatDongNgoaiTruongOptional.get().getGiangVien().getTaiKhoan(),
                    tieuDe, nDung, ThongBao.TrangThai.ChuaDoc
            );
            thongBaoService.luuThongBao(thongBao);
            hoatDongNgoaiTruongRepository.deleteById(maHoatDongNgoaiTruong);
            return ResponseEntity.ok(new MessageResponse("Xóa hoạt động ngoài trường thành công"));
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("cant-destroy"), HttpStatus.OK);
        }
    }
    @Transactional
    @DeleteMapping("/xoa/{maHoatDongNgoaiTruong}")
    public ResponseEntity<?> xoaHoatDongNgoaiTruong(@PathVariable Long maHoatDongNgoaiTruong) {
        try {
            Optional<HoatDongNgoaiTruong> hoatDongNgoaiTruongOptional = hoatDongNgoaiTruongRepository.findById(maHoatDongNgoaiTruong);
            if (hoatDongNgoaiTruongOptional.isEmpty()) {
                return new ResponseEntity<>(new MessageResponse("Hoạt động ngoài trường không tồn tại"), HttpStatus.NOT_FOUND);
            }
            String oldFile = hoatDongNgoaiTruongOptional.get().getFileMinhChung();
            if (oldFile != null) {
                storageService.delete(oldFile);
            }
            hoatDongNgoaiTruongRepository.deleteById(maHoatDongNgoaiTruong);
            return ResponseEntity.ok(new MessageResponse("Xóa hoạt động ngoài trường thành công"));
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("cant-delete"), HttpStatus.OK);
        }
    }
    @GetMapping("/{maHoatDongNgoaiTruong}/download")
    public ResponseEntity<?> downloadFile(@PathVariable Long maHoatDongNgoaiTruong) {
        Optional<HoatDongNgoaiTruong> hoatDongNgoaiTruong = hoatDongNgoaiTruongRepository.findById(maHoatDongNgoaiTruong);
        if (hoatDongNgoaiTruong.isPresent()) {
            try {
                String fileName = hoatDongNgoaiTruong.get().getFileMinhChung();
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
            return new ResponseEntity<>(new MessageResponse("Hoạt động ngoại trường không tồn tại"), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{maHoatDongNgoaiTruong}/ten-file")
    public ResponseEntity<?> getFileName(@PathVariable Long maHoatDongNgoaiTruong) {
        Optional<HoatDongNgoaiTruong> hoatDongNgoaiTruong = hoatDongNgoaiTruongRepository.findById(maHoatDongNgoaiTruong);
        if (hoatDongNgoaiTruong.isPresent()) {
            try {
                String fileName = hoatDongNgoaiTruong.get().getFileMinhChung();
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
    @GetMapping("/lay-danh-sach-nam")
    public List<Integer> getYears() {
        return hoatDongNgoaiTruongRepository.findYears();
    }
}
