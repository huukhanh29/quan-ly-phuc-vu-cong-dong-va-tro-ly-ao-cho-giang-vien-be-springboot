package com.quanly.hoatdongcongdong.controller;

import com.quanly.hoatdongcongdong.entity.ThongBao;
import com.quanly.hoatdongcongdong.exception.ResourceNotFoundException;
import com.quanly.hoatdongcongdong.payload.response.MessageResponse;
import com.quanly.hoatdongcongdong.repository.ThongBaoRepository;
import com.quanly.hoatdongcongdong.sercurity.services.TaiKhoanService;
import com.quanly.hoatdongcongdong.sercurity.services.ThongBaoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/thong-bao")
@CrossOrigin(value = "*")
public class ThongBaoController {
    @Autowired
    private ThongBaoService thongBaoService;
    @Autowired
    private TaiKhoanService taiKhoanService;
    @GetMapping()
    public ResponseEntity<?> layThongBaoTheoNguoiDungId(HttpServletRequest httpServletRequest) {
        Long maTk =  taiKhoanService.getCurrentUser(httpServletRequest).getMaTaiKhoan();
        List<ThongBao> thongBaos = thongBaoService.layThongBaoTheoTaiKhoan(maTk);
        return ResponseEntity.ok(thongBaos);
    }

    @PutMapping("/trang-thai/{maThongBao}")
    public ResponseEntity<?> datTrangThaiThongBao(@PathVariable Long maThongBao) {
        ThongBao thongBao = thongBaoService.layThongBaoTheoMaThongBao(maThongBao);
        thongBao.setTrangThai(ThongBao.TrangThai.DaDoc);
        thongBaoService.luuThongBao(thongBao);
        return ResponseEntity.ok("Da_doc_thong_bao");
    }

    @DeleteMapping("/xoa/{maThongBao}")
    public ResponseEntity<?> xoaThongBao(@PathVariable Long maThongBao) {
        thongBaoService.xoaThongBaoTheoMa(maThongBao);
        return ResponseEntity.ok("Da_xoa_thong_bao");
    }

    @DeleteMapping("/xoa-tat-ca")
    public ResponseEntity<?> xoaTatCaThongBaoTheoNguoiDungId(HttpServletRequest httpServletRequest) {
        Long maTk =  taiKhoanService.getCurrentUser(httpServletRequest).getMaTaiKhoan();
        List<ThongBao> thongBaos = thongBaoService.layThongBaoDaDocTheoTaiKhoan(maTk);
        if (!thongBaos.isEmpty()) {
            thongBaoService.xoaThongBaoDaDocTheoTaiKhoan(maTk);
            return ResponseEntity.ok("Đã xóa các thông báo cho người dùng có mã " + maTk);
        } else {
            return new ResponseEntity<>(new MessageResponse("KHÔNG TÌM THẤY"), HttpStatus.BAD_REQUEST);
        }
    }
}
