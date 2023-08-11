package com.quanly.hoatdongcongdong;

import com.quanly.hoatdongcongdong.entity.ChucDanh;
import com.quanly.hoatdongcongdong.entity.TaiKhoan;
import com.quanly.hoatdongcongdong.repository.ChucDanhRepository;
import com.quanly.hoatdongcongdong.repository.TaiKhoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableScheduling
public class HoatdongcongdongBeApplication implements CommandLineRunner {
    @Autowired
    private TaiKhoanRepository taiKhoanRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ChucDanhRepository chucDanhRepository;
    public static void main(String[] args) {
        SpringApplication.run(HoatdongcongdongBeApplication.class, args);
    }
    @Override
    public void run(String... args) throws Exception {
        if (!taiKhoanRepository.existsByQuyen(TaiKhoan.Quyen.QuanTriVien)) {
            // Tạo tài khoản mới với quyền là ADMIN
            TaiKhoan adminAccount = new TaiKhoan();
            adminAccount.setTenDayDu("Admin");
            adminAccount.setTenDangNhap("admin");
            adminAccount.setMatKhau(passwordEncoder.encode("admin123"));
            adminAccount.setEmail("admin@example.com");
            adminAccount.setQuyen(TaiKhoan.Quyen.QuanTriVien);
            adminAccount.setTrangthai(1);
            taiKhoanRepository.save(adminAccount);
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
