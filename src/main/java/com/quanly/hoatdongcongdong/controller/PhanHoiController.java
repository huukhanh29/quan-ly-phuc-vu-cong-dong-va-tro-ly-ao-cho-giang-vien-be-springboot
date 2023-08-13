package com.quanly.hoatdongcongdong.controller;

import com.quanly.hoatdongcongdong.entity.*;
import com.quanly.hoatdongcongdong.payload.request.PhanHoiRequest;
import com.quanly.hoatdongcongdong.payload.response.MessageResponse;
import com.quanly.hoatdongcongdong.payload.response.PhanHoiResponse;
import com.quanly.hoatdongcongdong.repository.*;
import io.jsonwebtoken.Claims;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import com.quanly.hoatdongcongdong.sercurity.jwt.JwtUtils;
import com.quanly.hoatdongcongdong.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;

import static com.quanly.hoatdongcongdong.sercurity.Helpers.getCurrentUser;

@RestController
@RequestMapping("/phan-hoi")
@CrossOrigin(value = "*")
public class PhanHoiController {
    @Autowired
    private PhanHoiRepository phanHoiRepository;
    @Autowired
    private TaiKhoanRepository taiKhoanRepository;
    @Autowired
    private CauHoiRepository cauHoiRepository;
    @Autowired
    private ThongBaoRepository thongBaoRepository;
    @Autowired
    private SinhVienRepository sinhVienRepository;

    @PostMapping("/them-moi")
    public ResponseEntity<?> createPhanHoi(@RequestBody PhanHoiRequest phanHoiRequest,
                                                 HttpServletRequest request) {
        TaiKhoan currentUser = getCurrentUser(request, taiKhoanRepository);
        Optional<SinhVien> sinhVien= sinhVienRepository.findById(currentUser.getMaTaiKhoan());
        PhanHoi phanHoiEntity = new PhanHoi();
        phanHoiEntity.setNoiDung(phanHoiRequest.getNoiDung());
        phanHoiEntity.setSinhVien(sinhVien.get());

        if (phanHoiRepository.findByNoiDung(phanHoiEntity.getNoiDung()) != null) {
            return new ResponseEntity<>(new MessageResponse("Phan_hoi_da_ton_tai"),
                    HttpStatus.BAD_REQUEST);
        }

        phanHoiRepository.save(phanHoiEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body("Them_phan_hoi_thanh_cong");
    }

    @GetMapping("/lay-danh-sach")
    public Page<PhanHoiResponse> getAllPhanHoi(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ngayTao") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir,
            @RequestParam(required = false, defaultValue = "") String searchTerm,
            @RequestParam(required = false) Long maTaiKhoan
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable paging = PageRequest.of(page, size, sort);

        Specification<PhanHoi> spec = Specification.where(null);

        if (!searchTerm.isEmpty()) {
            spec = spec.and((root, criteriaQuery, criteriaBuilder) -> {
                String pattern = "%" + searchTerm + "%";
                Join<PhanHoi, TaiKhoan> taiKhoanJoin = root.join("sinhVien").join("taiKhoan", JoinType.LEFT);
                Join<PhanHoi, CauHoi> cauHoiJoin = root.join("cauHoi", JoinType.LEFT);
                Predicate taiKhoanPredicate = criteriaBuilder.like(taiKhoanJoin.get("tenDayDu"), pattern);
                Predicate cauHoiPredicate = criteriaBuilder.like(cauHoiJoin.get("cauHoi"), pattern);
                Predicate noiDungPredicate = criteriaBuilder.like(root.get("noiDung"), pattern);
                return criteriaBuilder.or(
                        criteriaBuilder.and(taiKhoanPredicate, cauHoiPredicate, noiDungPredicate),
                        taiKhoanPredicate,
                        cauHoiPredicate,
                        noiDungPredicate
                );
            });
        }

        if (maTaiKhoan != null) {
            spec = spec.and((root, criteriaQuery, criteriaBuilder) -> {
                return criteriaBuilder.equal(root.get("sinhVien").get("taiKhoan").get("maTaiKhoan"), maTaiKhoan);
            });
        }

        Page<PhanHoi> phanHois = phanHoiRepository.findAll(spec, paging);

        return phanHois.map(PhanHoiResponse::fromEntity);
    }

    @DeleteMapping("/xoa/{id}")
    public ResponseEntity<?> deletePhanHoi(@PathVariable(value = "id") Long phanHoiId) {
        PhanHoi phanHoi = phanHoiRepository.findById(phanHoiId)
                .orElseThrow(() -> new ResourceNotFoundException("PhanHoi", "maPhanHoi", phanHoiId));

        phanHoiRepository.delete(phanHoi);

        ThongBao thongBao = new ThongBao();
        Optional<TaiKhoan> taiKhoan = taiKhoanRepository.findByTenDangNhap(phanHoi.getSinhVien().getTaiKhoan().getTenDangNhap());
        thongBao.setTieuDe("Xóa phản hồi");
        thongBao.setNoiDung("Phản hồi " + phanHoi.getNoiDung() +
                " của bạn đã bị xóa và không được phản hồi do có nội dung hỏi không hợp lý.");
        thongBao.setTaiKhoan(taiKhoan.get());
        thongBao.setTrangThai(ThongBao.TrangThai.ChuaDoc);
        thongBaoRepository.save(thongBao);
        return ResponseEntity.ok("Da_xoa");
    }

    @DeleteMapping("/xoa-tat-ca")
    public ResponseEntity<?> deleteAllPhanHoi() {
        List<PhanHoi> phanHois = phanHoiRepository.findAllByCauHoiNotNull();
        if (!phanHois.isEmpty()) {
            phanHoiRepository.deleteByCauHoiNotNull();
            return ResponseEntity.ok("Da_xoa_cac_phan_hoi_da_tra_loi");
        } else {
            return new ResponseEntity<>(new MessageResponse("NOT FOUND"), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/tra-loi/{id}")
    public ResponseEntity<?> replyToPhanHoi(@RequestBody CauHoi cauHoi,
                                            @PathVariable(value = "id") Long phanHoiId) {
        if (cauHoiRepository.findByCauHoi(cauHoi.getCauHoi()) != null) {
            return new ResponseEntity<>(new MessageResponse("Question already exists"), HttpStatus.CONFLICT);
        } else {
            cauHoiRepository.save(cauHoi);
            CauHoi newCauHoi = cauHoiRepository.findTopByOrderByMaCauHoiDesc();
            PhanHoi phanHoi = phanHoiRepository.findById(phanHoiId)
                    .orElseThrow(() -> new ResourceNotFoundException("PhanHoi", "id", phanHoiId));
            phanHoi.setCauHoi(newCauHoi);
            phanHoiRepository.save(phanHoi);

            ThongBao thongBao = new ThongBao();
            Optional<TaiKhoan> taiKhoan = taiKhoanRepository.findByTenDangNhap(phanHoi.getSinhVien().getTaiKhoan().getTenDangNhap());
            thongBao.setNoiDung("Câu hỏi " +  phanHoi.getNoiDung()  +
                    " của bạn đã được phản hồi vui lòng hỏi chatbot với từ khóa " + newCauHoi.getCauHoi() + ".");
            thongBao.setTieuDe("Trả lời phản hồi");
            thongBao.setTaiKhoan(taiKhoan.get());
            thongBao.setTrangThai(ThongBao.TrangThai.ChuaDoc);
            thongBaoRepository.save(thongBao);
            return ResponseEntity.ok(cauHoi);
        }
    }
}

