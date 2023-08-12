package com.quanly.hoatdongcongdong.sercurity.services;

import com.quanly.hoatdongcongdong.entity.TaiKhoan;
import com.quanly.hoatdongcongdong.repository.TaiKhoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TaiKhoanService {
    @Autowired
    private TaiKhoanRepository taiKhoanRepository;
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
}
