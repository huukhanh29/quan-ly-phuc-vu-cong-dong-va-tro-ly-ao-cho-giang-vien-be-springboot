package com.quanly.hoatdongcongdong.controller;

import com.quanly.hoatdongcongdong.entity.*;
import com.quanly.hoatdongcongdong.payload.request.PhanHoiRequest;
import com.quanly.hoatdongcongdong.payload.response.MessageResponse;
import com.quanly.hoatdongcongdong.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import com.quanly.hoatdongcongdong.exception.ResourceNotFoundException;
import java.util.Optional;


@RestController
@RequestMapping("/api/phan-hoi")
@CrossOrigin(value = "*")
public class PhanHoiController {

    private final PhanHoiService phanHoiService;
    private final TaiKhoanService taiKhoanService;
    private final CauHoiService cauHoiService;
    private final ThongBaoService thongBaoService;
    private final SinhVienService sinhVienService;

    @Autowired
    public PhanHoiController(
            PhanHoiService phanHoiService,
            TaiKhoanService taiKhoanService,
            CauHoiService cauHoiService,
            ThongBaoService thongBaoService,
            SinhVienService sinhVienService) {
        this.phanHoiService = phanHoiService;
        this.taiKhoanService = taiKhoanService;
        this.cauHoiService = cauHoiService;
        this.thongBaoService = thongBaoService;
        this.sinhVienService =sinhVienService;
    }

    @PostMapping("/them-moi")
    public ResponseEntity<?> createPhanHoi(@RequestBody PhanHoiRequest phanHoiRequest,
                                           HttpServletRequest request) {
        if (phanHoiService.findByNoiDung(phanHoiRequest.getNoiDung()) != null) {
            return new ResponseEntity<>(new MessageResponse("exist"),
                    HttpStatus.OK);
        }
        // Lấy thông tin người dùng hiện tại
        Optional<SinhVien> sinhVien = sinhVienService.findById(taiKhoanService.getCurrentUser(request).getMaTaiKhoan());
        if(sinhVien.isPresent()){
            phanHoiService.createPhanHoi(phanHoiRequest.getNoiDung(), sinhVien.get());
        }else {
            return new ResponseEntity<>(new MessageResponse("Sinh viên không tồn tại"),
                    HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(new MessageResponse("OK"),
                HttpStatus.CREATED);
    }

    @GetMapping("/lay-danh-sach")
    public Page<PhanHoi> getAllPhanHoi(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ngayTao") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir,
            @RequestParam(required = false, defaultValue = "") String searchTerm,
            @RequestParam(required = false) String tenDangNhap
    ) {
        Page<PhanHoi> phanHois = phanHoiService.getAllPhanHoi(page, size, sortBy, sortDir, searchTerm, tenDangNhap);
        return phanHois;
    }

    @DeleteMapping("/xoa/{ma}")
    public ResponseEntity<?> deletePhanHoi(@PathVariable(value = "ma") Long phanHoiId) {
        try {
            phanHoiService.deletePhanHoiById(phanHoiId);
            return ResponseEntity.ok("Đã xóa");
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(new MessageResponse("Câu hỏi không tồn tại!"), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("Không thể xóa câu hỏi."), HttpStatus.BAD_REQUEST);
        }
    }
    @DeleteMapping("/xoa-tat-ca")
    public ResponseEntity<?> deleteAllPhanHoi() {
        String result = phanHoiService.deleteAllPhanHoi();
        if (result.equals("success")) {
            return ResponseEntity.ok("Đã xóa tất cả các phản hồi đã được trả lời");
        } else {
            return new ResponseEntity<>(new MessageResponse("Không tìm thấy phản hồi đã được trả lời"), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/tra-loi/{id}")
    public ResponseEntity<?> replyToPhanHoi(@RequestBody CauHoi cauHoi,
                                            @PathVariable(value = "id") Long phanHoiId) {
        try{
            if (cauHoiService.findByCauHoi(cauHoi.getCauHoi()) != null) {
                return new ResponseEntity<>(new MessageResponse("Câu hỏi đã tồn tại"), HttpStatus.CONFLICT);
            } else {
                phanHoiService.replyToPhanHoi(cauHoi, phanHoiId);
                return ResponseEntity.ok(cauHoi);
            }
        }catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(new MessageResponse("Phản hồi không tồn tại!"), HttpStatus.NOT_FOUND);
        }

    }
}
