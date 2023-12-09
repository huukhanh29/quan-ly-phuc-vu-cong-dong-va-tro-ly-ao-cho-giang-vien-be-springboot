package com.quanly.hoatdongcongdong.controller;

import com.quanly.hoatdongcongdong.entity.TaiKhoan;
import com.quanly.hoatdongcongdong.entity.ThongBao;
import com.quanly.hoatdongcongdong.payload.response.MessageResponse;
import com.quanly.hoatdongcongdong.service.TaiKhoanService;
import com.quanly.hoatdongcongdong.service.ThongBaoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/thong-bao")
@CrossOrigin(value = "*")
public class ThongBaoController {
    @Autowired
    private ThongBaoService thongBaoService;
    @Autowired
    private TaiKhoanService taiKhoanService;
    @Autowired
    private SimpMessageSendingOperations messagingTemplate;
    @GetMapping()
    public ResponseEntity<?> layThongBaoTheoNguoiDungId(HttpServletRequest httpServletRequest) {
        Long maTk =  taiKhoanService.getCurrentUser(httpServletRequest).getMaTaiKhoan();
        List<ThongBao> thongBaos = thongBaoService.layThongBaoTheoTaiKhoan(maTk);
        return ResponseEntity.ok(thongBaos);
    }
    //lấy số thông báo chưa đọc
    @GetMapping("/chua-doc")
    public ResponseEntity<Long> demThongBaoChuaDoc(HttpServletRequest httpServletRequest) {
        Long maTk =  taiKhoanService.getCurrentUser(httpServletRequest).getMaTaiKhoan();
        Long soLuongChuaDoc = thongBaoService.soThongBaoChuaDocTheoTaiKhoan(maTk);
        return ResponseEntity.ok(soLuongChuaDoc);
    }
    @PutMapping("/trang-thai/{maThongBao}")
    public ResponseEntity<?> datTrangThaiThongBao(@PathVariable Long maThongBao) {
        ThongBao thongBao = thongBaoService.layThongBaoTheoMaThongBao(maThongBao);
        thongBao.setTrangThai(ThongBao.TrangThai.DaDoc);
        thongBaoService.luuThongBao(thongBao);
        messagingTemplate.convertAndSendToUser(thongBao.getTaiKhoan().getTenDangNhap(), "/queue/messages", "update-status");
        return ResponseEntity.ok(new MessageResponse("đã đọc"));
    }
    @PutMapping("/trang-thai-tat-ca/{tenDangNhap}")
    public ResponseEntity<?> datTrangThaiThongBaoAll(@PathVariable String tenDangNhap) {
        Optional<TaiKhoan> taiKhoanOpt = taiKhoanService.findByTenDangNhap(tenDangNhap);
        if (!taiKhoanOpt.isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Tài khoản không tồn tại"));
        }

        List<ThongBao> thongBaoList = thongBaoService.layThongBaoChuaDocTheoTaiKhoan(taiKhoanOpt.get().getMaTaiKhoan());
        for (ThongBao thongBao : thongBaoList) {
            thongBao.setTrangThai(ThongBao.TrangThai.DaDoc);
            thongBaoService.luuThongBao(thongBao);
        }

        messagingTemplate.convertAndSendToUser(tenDangNhap, "/queue/messages", "update-status");
        return ResponseEntity.ok(new MessageResponse("Tất cả thông báo đã được đánh dấu là đã đọc"));
    }

    @DeleteMapping("/xoa/{maThongBao}")
    public ResponseEntity<?> xoaThongBao(@PathVariable Long maThongBao) {
        thongBaoService.xoaThongBaoTheoMa(maThongBao);
        return ResponseEntity.ok(new MessageResponse("deleted"));
    }

    @DeleteMapping("/xoa-tat-ca")
    public ResponseEntity<?> xoaTatCaThongBaoTheoNguoiDungId(HttpServletRequest httpServletRequest) {
        Long maTk =  taiKhoanService.getCurrentUser(httpServletRequest).getMaTaiKhoan();
        List<ThongBao> thongBaos = thongBaoService.layThongBaoDaDocTheoTaiKhoan(maTk);
        if (!thongBaos.isEmpty()) {
            thongBaoService.xoaThongBaoDaDocTheoTaiKhoan(maTk);
            return  new ResponseEntity<>(new MessageResponse("Đã xóa các thông báo cho người dùng"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new MessageResponse("Not_Found"), HttpStatus.NOT_FOUND);
        }
    }
}
