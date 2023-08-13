package com.quanly.hoatdongcongdong.controller;
import com.quanly.hoatdongcongdong.entity.*;
import com.quanly.hoatdongcongdong.exception.ResourceNotFoundException;
import com.quanly.hoatdongcongdong.payload.request.CauHoiRequest;
import com.quanly.hoatdongcongdong.payload.response.MessageResponse;
import com.quanly.hoatdongcongdong.repository.*;
import com.quanly.hoatdongcongdong.sercurity.jwt.JwtUtils;
import com.quanly.hoatdongcongdong.sercurity.services.TaiKhoanService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.quanly.hoatdongcongdong.sercurity.Helpers.*;
@RestController
@RequestMapping("/cau-hoi")
@CrossOrigin(value = "*")
public class CauHoiController {
    @Autowired
    private CauHoiRepository cauHoiRepository;
    @Autowired
    private PhanHoiRepository phanHoiRepository;
    @Autowired
    private LichSuRepository lichSuRepository;
    @Autowired
    private SinhVienRepository sinhVienRepository;
    @Autowired
    private TaiKhoanRepository taiKhoanRepository;
    @Autowired
    private TaiKhoanService taiKhoanService;
    @PostMapping("/dat-cau-hoi")
    public ResponseEntity<?> getAnswer(@RequestBody CauHoiRequest request,
                                       HttpServletRequest httpServletRequest) {
        String question = request.getCauHoi().toLowerCase();
        List<CauHoi> cauHoiList = cauHoiRepository.findAll();
        List<Float> percSame = new ArrayList<>();
        List<Float> same = new ArrayList<>();
        for (CauHoi cauHoi : cauHoiList) {
            String query = cauHoi.getCauHoi();
            String q = query.toLowerCase();
            float a = calculateSimilarity(createSlug(question), createSlug(q));
            percSame.add(a);
        }
        for (CauHoi cauHoi : cauHoiList) {
            String query = cauHoi.getCauHoi();
            String q = query.toLowerCase();
            float b = calculateSimilarity(createSlug(question), createSlug(q));
            same.add(b);
            float maxSame = Collections.max(same);
            float maxPercSame = Collections.max(percSame);
            if (maxSame == maxPercSame && maxPercSame != 0 && b >= 0.5) {
                question = cauHoi.getCauHoi();
                break;
            }
        }
        CauHoi cauHoi = cauHoiRepository.findByCauHoi(question);
        if (cauHoi != null) {
            //thêm vào lịch sử khi thực hiện hỏi thành công
            LichSu lichSu = new LichSu();
            TaiKhoan currentUser = taiKhoanService.getCurrentUser(httpServletRequest);
            Optional<SinhVien> sinhVien= sinhVienRepository.findById(currentUser.getMaTaiKhoan());
            if (sinhVien.isPresent()) {
                lichSu.setSinhVien(sinhVien.get());
                lichSu.setCauHoi(cauHoi);
                lichSuRepository.save(lichSu);
            } else {
                return new ResponseEntity<>(new MessageResponse("Khong_tim_thay_sinh_vien"), HttpStatus.BAD_REQUEST);
            }
            return ResponseEntity.ok(cauHoi);
        } else {
            return new ResponseEntity<String>(new String("unknown"),HttpStatus.OK);
        }
    }

    @PostMapping("/them-moi")
    public ResponseEntity<?> createCauHoi(@RequestBody CauHoi cauHoi) {
        if (cauHoiRepository.findByCauHoi(cauHoi.getCauHoi()) != null) {
            return new ResponseEntity<>(new MessageResponse("Question already exists"), HttpStatus.CONFLICT);
        } else {
            cauHoiRepository.save(cauHoi);
            return ResponseEntity.ok(cauHoi);
        }
    }

    @GetMapping("/lay-danh-sach")
    public Page<CauHoi> getAllCauHoi(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "maCauHoi") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir,
            @RequestParam(required = false, defaultValue = "") String searchTerm
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable paging = PageRequest.of(page, size, sort);

        Specification<CauHoi> spec = Specification.where(null);

        if (!searchTerm.isEmpty()) {
            spec = spec.and((root, criteriaQuery, criteriaBuilder) -> {
                String pattern = "%" + searchTerm + "%";
                return criteriaBuilder.or(
                        criteriaBuilder.like(root.get("cauHoi"), pattern)
                );
            });
        }
        return cauHoiRepository.findAll(spec, paging);
    }

    @GetMapping("/{ma}")
    public CauHoi getCauHoiById(@PathVariable(value = "ma") Long cauHoiId) {
        return cauHoiRepository.findById(cauHoiId)
                .orElseThrow(() -> new ResourceNotFoundException("CauHoi", "maCauHoi", cauHoiId));
    }

    @PutMapping("/cap-nhat/{ma}")
    public CauHoi updateCauHoi(@PathVariable(value = "ma") Long cauHoiId,
                               @RequestBody CauHoi cauHoiDetails) {
        CauHoi cauHoi = cauHoiRepository.findById(cauHoiId)
                .orElseThrow(() -> new ResourceNotFoundException("CauHoi", "maCauHoi", cauHoiId));

        cauHoi.setCauHoi(cauHoiDetails.getCauHoi());
        cauHoi.setTraLoi(cauHoiDetails.getTraLoi());

        return cauHoiRepository.save(cauHoi);
    }

    @DeleteMapping("/xoa/{ma}")
    public ResponseEntity<?> deleteCauHoi(@PathVariable(value = "ma") Long cauHoiId) {
        CauHoi cauHoi = cauHoiRepository.findById(cauHoiId)
                .orElseThrow(() -> new ResourceNotFoundException("CauHoi", "maCauHoi", cauHoiId));
        PhanHoi feedback = phanHoiRepository.findFirstByCauHoi_MaCauHoi(cauHoiId);
        LichSu historyEntity = lichSuRepository.findFirstByCauHoi_MaCauHoi(cauHoiId);
        if (feedback != null || historyEntity != null) {
            return new ResponseEntity<>(new MessageResponse("Da_su_dung"), HttpStatus.BAD_REQUEST);
        }
        cauHoiRepository.delete(cauHoi);

        return ResponseEntity.ok("Đã xóa");
    }

    @GetMapping("/tong-cau-hoi")
    public Long countCauHoi() {
        return cauHoiRepository.count();
    }
}

