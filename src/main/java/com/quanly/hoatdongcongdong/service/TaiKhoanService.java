package com.quanly.hoatdongcongdong.service;

import com.quanly.hoatdongcongdong.entity.ChucDanh;
import com.quanly.hoatdongcongdong.entity.GiangVien;
import com.quanly.hoatdongcongdong.entity.SinhVien;
import com.quanly.hoatdongcongdong.entity.TaiKhoan;
import com.quanly.hoatdongcongdong.repository.GiangVienRepository;
import com.quanly.hoatdongcongdong.repository.SinhVienRepository;
import com.quanly.hoatdongcongdong.repository.TaiKhoanRepository;
import com.quanly.hoatdongcongdong.sercurity.jwt.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TaiKhoanService {
    @Autowired
    private TaiKhoanRepository taiKhoanRepository;
    @Autowired
    private GiangVienRepository giangVienRepository;
    @Autowired
    private SinhVienRepository sinhVienRepository;
    @Autowired
    PasswordEncoder encoder;
    public Optional<TaiKhoan> findByTenDangNhap(String ten) {

        return taiKhoanRepository.findByTenDangNhap(ten);
    }

    public Boolean existsByTenDangNhap(String ten) {
        return taiKhoanRepository.existsByTenDangNhap(ten);
    }

    public Boolean isTaiKhoanKhoa(String ten) {
        Optional<TaiKhoan> taiKhoan = findByTenDangNhap(ten);
        return taiKhoan.isPresent() && taiKhoan.get().getTrangthai().equals(TaiKhoan.TrangThai.Khoa);
    }

    public Boolean isMatKhauHopLe(String matKhau, String encodedMatKhau) {
        return encoder.matches(matKhau, encodedMatKhau);
    }
    public void capNhatThongTinNguoiDung(Long id, String soDienThoai, Date ngaySinh,
                                         TaiKhoan.GioiTinh gioiTinh, String diaChi) {
        TaiKhoan taiKhoan = taiKhoanRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("taikhoan-notfound"));

        taiKhoan.capNhatThongTin(soDienThoai, ngaySinh, gioiTinh, diaChi);
        taiKhoanRepository.save(taiKhoan);
    }
    public TaiKhoan getCurrentUser(HttpServletRequest httpServletRequest) {
        String token = JwtUtils.resolveToken(httpServletRequest);
        if (token == null || !JwtUtils.validateJwtToken(token)) {
            // Hoặc bạn có thể trả về null hoặc một giá trị thích hợp khác tùy theo yêu cầu của bạn
            return null;
        }

        Claims claims = JwtUtils.getClaimsFromToken(token);
        Long currentUserId = claims.get("id", Long.class);
        return taiKhoanRepository.findById(currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("taikhoan-notfound"));
    }
    public void capNhatMatKhauNguoiDung(Long maTaiKhoan, String matKhauMoi) {
        Optional<TaiKhoan> optionalTaiKhoan = taiKhoanRepository.findById(maTaiKhoan);

        if (optionalTaiKhoan.isPresent()) {
            TaiKhoan taiKhoan = optionalTaiKhoan.get();

            // Update mật khẩu mới
            taiKhoan.setMatKhau(encoder.encode(matKhauMoi));

            taiKhoanRepository.save(taiKhoan);
        } else {
            throw new EntityNotFoundException("taikhoan-notfound");
        }
    }
    @Transactional
    public void themMoiSinhVien(String tenDangNhap, String matKhau, String email, TaiKhoan.Quyen quyen,
                                String tenDayDu, TaiKhoan.GioiTinh gioiTinh, Year namNhapHoc) {
        if (existsByTenDangNhap(tenDangNhap)) {
            throw new EntityExistsException("username-exist");
        }
        if (taiKhoanRepository.existsByEmail(email)) {
            throw new EntityExistsException("email-exist");
        }
        TaiKhoan taiKhoan = new TaiKhoan(tenDangNhap, encoder.encode(matKhau), email,
                quyen, tenDayDu,gioiTinh);
        taiKhoanRepository.save(taiKhoan);

        SinhVien sinhVien = new SinhVien();
        sinhVien.setTaiKhoan(taiKhoan);
        sinhVien.setNamNhapHoc(namNhapHoc);
        sinhVienRepository.save(sinhVien);
    }
    @Transactional
    public void themMoiGiangVien(String tenDangNhap, String matKhau, String email, TaiKhoan.Quyen quyen,
                                 String tenDayDu, TaiKhoan.GioiTinh gioiTinh, ChucDanh chucDanh) {
        if (existsByTenDangNhap(tenDangNhap)) {
            throw new EntityExistsException("username-exist");
        }
        if (taiKhoanRepository.existsByEmail(email)) {
            throw new EntityExistsException("email-exist");
        }
        TaiKhoan taiKhoan = new TaiKhoan(tenDangNhap, encoder.encode(matKhau), email,
                quyen, tenDayDu,gioiTinh);
        taiKhoanRepository.save(taiKhoan);

        GiangVien giangVien = new GiangVien();
        giangVien.setTaiKhoan(taiKhoan);
        giangVien.setChucDanh(chucDanh);
        giangVienRepository.save(giangVien);
    }

}
