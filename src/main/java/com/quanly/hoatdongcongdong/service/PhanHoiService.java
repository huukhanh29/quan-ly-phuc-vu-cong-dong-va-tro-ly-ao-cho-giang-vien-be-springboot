package com.quanly.hoatdongcongdong.service;


import com.quanly.hoatdongcongdong.entity.*;
import com.quanly.hoatdongcongdong.exception.ResourceNotFoundException;
import com.quanly.hoatdongcongdong.payload.response.PhanHoiResponse;
import com.quanly.hoatdongcongdong.repository.PhanHoiRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PhanHoiService {
    @Autowired
    private ThongBaoService thongBaoService;
    @Autowired
    private TaiKhoanService taiKhoanService;
    @Autowired
    private CauHoiService cauHoiService;
    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    private final PhanHoiRepository phanHoiRepository;


    @Autowired
    public PhanHoiService(PhanHoiRepository phanHoiRepository) {
        this.phanHoiRepository = phanHoiRepository;
    }
    public PhanHoi findByNoiDung(String noiDung) {
        return phanHoiRepository.findByNoiDung(noiDung);
    }

    public Page<PhanHoi> findAll(Pageable pageable) {
        return phanHoiRepository.findAll(pageable);
    }

    public PhanHoi findFirstByCauHoi_MaCauHoi(Long maCauHoi) {
        return phanHoiRepository.findFirstByCauHoi_MaCauHoi(maCauHoi);
    }

    @Transactional
    public void deleteByCauHoiNotNull() {
        phanHoiRepository.deleteByCauHoiNotNull();
    }

    public List<PhanHoi> findAllByCauHoiNotNull() {
        return phanHoiRepository.findAllByCauHoiNotNull();
    }
    public PhanHoi savePhanHoi(PhanHoi phanHoi) {
        return phanHoiRepository.save(phanHoi);
    }
    public PhanHoi findById(Long id) {
        Optional<PhanHoi> optionalPhanHoi = phanHoiRepository.findById(id);
        return optionalPhanHoi.orElse(null);
    }
    public Page<PhanHoi> getAllPhanHoi(int page, int size, String sortBy, String sortDir, String searchTerm, String tenDangNhap) {
        Pageable paging = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDir), sortBy));

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

        if (!Objects.equals(tenDangNhap, "")) {
            spec = spec.and((root, criteriaQuery, criteriaBuilder) -> {
                return criteriaBuilder.equal(root.get("sinhVien").get("taiKhoan").get("tenDangNhap"), tenDangNhap);
            });
        }

        return phanHoiRepository.findAll(spec, paging);
    }

    public void deletePhanHoiById(Long phanHoiId) {
        Optional<PhanHoi> optionalPhanHoi = phanHoiRepository.findById(phanHoiId);
        if (optionalPhanHoi.isPresent()) {
            phanHoiRepository.deleteById(phanHoiId);
        } else {
            throw new EntityNotFoundException("phanhoi-notfound");
        }

    }
    public String deleteAllPhanHoi() {
        List<PhanHoi> phanHois = phanHoiRepository.findAllByCauHoiNotNull();
        if (!phanHois.isEmpty()) {
            phanHoiRepository.deleteByCauHoiNotNull();
        }
        return "success";
    }
    public void createPhanHoi(String noiDung, SinhVien sinhVien) {
        messagingTemplate.convertAndSendToUser("admin", "/queue/messages", "send-feedback");
        PhanHoi phanHoiEntity = new PhanHoi();
        phanHoiEntity.setNoiDung(noiDung);
        phanHoiEntity.setSinhVien(sinhVien);
        phanHoiRepository.save(phanHoiEntity);
    }
    public void replyToPhanHoi(CauHoi cauHoi, Long phanHoiId) {
        CauHoi newCauHoi;
        if(!cauHoiService.existsByCauHoi(cauHoi.getCauHoi())){
             newCauHoi = cauHoiService.saveCauHoi(cauHoi);
        }else {
            newCauHoi =cauHoi;
        }

        Optional<PhanHoi> phanHoiOptional = phanHoiRepository.findById(phanHoiId);
        if (phanHoiOptional.isPresent()) {
            PhanHoi phanHoi = phanHoiOptional.get();
            phanHoi.setCauHoi(newCauHoi);
            phanHoiRepository.save(phanHoi);
            messagingTemplate.convertAndSendToUser(phanHoi.getSinhVien().getTaiKhoan().getTenDangNhap(), "/queue/messages", "reply-feedback");
            Optional<TaiKhoan> taiKhoan = taiKhoanService.findByTenDangNhap(phanHoi.getSinhVien().getTaiKhoan().getTenDangNhap());
            String tieuDe = "Trả lời phản hồi";
            String noiDung = "Câu hỏi " + phanHoi.getNoiDung() +
                    " của bạn đã được phản hồi. Vui lòng hỏi chatbot với từ khóa \"" + newCauHoi.getCauHoi() + "\".";
            ThongBao thongBao = thongBaoService.taoMoiThongBao(
                    taiKhoan.get(), tieuDe, noiDung, ThongBao.TrangThai.ChuaDoc
            );
            thongBaoService.luuThongBao(thongBao);
        }else {
            throw new EntityNotFoundException("phanhoi-notfound");
        }

    }

}
