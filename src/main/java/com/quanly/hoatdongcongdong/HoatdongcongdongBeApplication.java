package com.quanly.hoatdongcongdong;

import com.quanly.hoatdongcongdong.entity.ChucDanh;
import com.quanly.hoatdongcongdong.entity.SinhVien;
import com.quanly.hoatdongcongdong.entity.TaiKhoan;
import com.quanly.hoatdongcongdong.repository.ChucDanhRepository;
import com.quanly.hoatdongcongdong.repository.SinhVienRepository;
import com.quanly.hoatdongcongdong.repository.TaiKhoanRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement
public class HoatdongcongdongBeApplication implements CommandLineRunner {
    @Autowired
    private TaiKhoanRepository taiKhoanRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ChucDanhRepository chucDanhRepository;
    @Autowired
    private SinhVienRepository sinhVienRepository;
    @PersistenceContext
    private EntityManager entityManager;
    public static void main(String[] args) {
        SpringApplication.run(HoatdongcongdongBeApplication.class, args);
    }
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (!taiKhoanRepository.existsByQuyen(TaiKhoan.Quyen.QuanTriVien)) {
            // Tạo tài khoản mới với quyền là ADMIN
            TaiKhoan adminAccount = new TaiKhoan();
            adminAccount.setTenDayDu("Admin");
            adminAccount.setTenDangNhap("admin");
            adminAccount.setMatKhau(passwordEncoder.encode("admin123"));
            adminAccount.setEmail("admin@example.com");
            adminAccount.setQuyen(TaiKhoan.Quyen.QuanTriVien);
            adminAccount.setTrangthai(TaiKhoan.TrangThai.Mo);
            taiKhoanRepository.save(adminAccount);
        }
        if (!taiKhoanRepository.existsByQuyen(TaiKhoan.Quyen.SinhVien)) {
            // Tạo tài khoản mới với quyền là SinhVien
            TaiKhoan taiKhoan = new TaiKhoan();
            taiKhoan.setTenDayDu("Hữu Khanh");
            taiKhoan.setTenDangNhap("khanh");
            taiKhoan.setMatKhau(passwordEncoder.encode("khanh123"));
            taiKhoan.setEmail("khanh@example.com");
            taiKhoan.setQuyen(TaiKhoan.Quyen.SinhVien);
            taiKhoan.setTrangthai(TaiKhoan.TrangThai.Mo);
            TaiKhoan mergedTaiKhoan = entityManager.merge(taiKhoan);
            SinhVien sinhVien = new SinhVien();
            sinhVien.setChuyenNganh("IT");
            sinhVien.setNamNhapHoc(Year.of(2019));
            sinhVien.setTaiKhoan(mergedTaiKhoan);
            sinhVienRepository.save(sinhVien);
        }
        if (chucDanhRepository.count() == 0) {
            List<ChucDanh> chucDanhs = new ArrayList<>();

            chucDanhs.add(new ChucDanh("Giảng viên cao cấp có chức danh Giáo sư", 58));
            chucDanhs.add(new ChucDanh("Giảng viên cao cấp có chức danh Phó Giáo sư", 48));
            chucDanhs.add(new ChucDanh("Giảng viên cao cấp không có chức danh Giáo sư, Phó Giáo sư", 70));
            chucDanhs.add(new ChucDanh("Giảng viên chính có hệ số lương >=5,76", 40));
            chucDanhs.add(new ChucDanh("Giảng viên chính(Hạng II) có hệ số lương >=4,40", 55));
            chucDanhs.add(new ChucDanh("Giảng viên chính(Hạng III) có hệ số lương >=4,32", 58));
            chucDanhs.add(new ChucDanh("Giảng viên chính(Hạng III) có hệ số lương >=3,33", 110));
            chucDanhs.add(new ChucDanh("Giảng viên chính(Hạng III) có hệ số lương >=3,00", 150));
            chucDanhs.add(new ChucDanh("Giảng viên trong thời gian tập sự(hưởng 85% lương khởi điểm)", 403));
            chucDanhs.add(new ChucDanh("Trợ giảng", 440));

            chucDanhRepository.saveAll(chucDanhs);
        }
    }
}
