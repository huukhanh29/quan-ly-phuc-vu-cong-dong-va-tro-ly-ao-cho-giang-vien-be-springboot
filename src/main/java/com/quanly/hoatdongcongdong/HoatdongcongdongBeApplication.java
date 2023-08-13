package com.quanly.hoatdongcongdong;

import com.quanly.hoatdongcongdong.entity.*;
import com.quanly.hoatdongcongdong.repository.*;
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
    @Autowired
    private GiangVienRepository giangVienRepository;
    @Autowired
    private LoaiHoatDongRepository loaiHoatDongRepository;

    @PersistenceContext
    private EntityManager entityManager;
    public static void main(String[] args) {
        SpringApplication.run(HoatdongcongdongBeApplication.class, args);
    }
    @Override
    @Transactional
    public void run(String... args) throws Exception {
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
        if (loaiHoatDongRepository.count() == 0) {
            List<LoaiHoatDong> loaiHoatDongs = new ArrayList<>();

            loaiHoatDongs.add(new LoaiHoatDong("Văn hóa và nghệ thuật", "Triển lãm nghệ thuật, diễn hát, biểu diễn âm nhạc, sự kiện văn hóa dân gian, v.v."));
            loaiHoatDongs.add(new LoaiHoatDong("Thể thao và giải trí", "Giải bóng đá cộng đồng, cuộc thi thể thao, chương trình thể dục ngoài trời, v.v."));
            loaiHoatDongs.add(new LoaiHoatDong("Giáo dục và tư vấn", "Buổi thảo luận về các vấn đề xã hội, lớp học hướng nghiệp, tư vấn về sức khỏe tâm thần, v.v."));
            loaiHoatDongs.add(new LoaiHoatDong("Bảo vệ môi trường", "Dọn dẹp môi trường, thảo luận về biện pháp bảo vệ môi trường, chương trình tái chế, v.v."));
            loaiHoatDongs.add(new LoaiHoatDong("Xã hội và từ thiện", "Quyên góp thực phẩm cho người nghèo, chăm sóc người cao tuổi, tặng quà cho trẻ em khó khăn, v.v."));
            loaiHoatDongs.add(new LoaiHoatDong("Kiến thức và kỹ năng", "Khóa học, buổi học chia sẻ kinh nghiệm, hướng dẫn kỹ năng làm việc, v.v."));
            loaiHoatDongs.add(new LoaiHoatDong("Kết nối cộng đồng", "Hội thảo, họp mặt cộng đồng, cuộc thi giao tiếp, chương trình networking, v.v."));
            loaiHoatDongs.add(new LoaiHoatDong("Tôn giáo và tâm linh", "Buổi lễ, buổi thảo luận về tôn giáo, chương trình thiền và yoga, v.v."));
            loaiHoatDongs.add(new LoaiHoatDong("Vui chơi và giải trí", "Cuộc thi trò chơi, chương trình vui nhộn, ngày hội gia đình, v.v."));

            loaiHoatDongRepository.saveAll(loaiHoatDongs);
        }
        if (!taiKhoanRepository.existsByQuyen(TaiKhoan.Quyen.QuanTriVien)) {
            // Tạo tài khoản mới với quyền là ADMIN
            TaiKhoan adminAccount = new TaiKhoan();
            adminAccount.setTenDayDu("Admin");
            adminAccount.setTenDangNhap("admin");
            adminAccount.setMatKhau(passwordEncoder.encode("admin123"));
            adminAccount.setEmail("admin@example.com");
            adminAccount.setQuyen(TaiKhoan.Quyen.QuanTriVien);
            adminAccount.setTrangthai(TaiKhoan.TrangThai.Mo);
            adminAccount.setGioiTinh(TaiKhoan.GioiTinh.Nam);
            taiKhoanRepository.save(adminAccount);
        }
        if (!taiKhoanRepository.existsByQuyen(TaiKhoan.Quyen.SinhVien)) {
            // Tạo tài khoản mới với quyền là SinhVien
            TaiKhoan taiKhoanSv = new TaiKhoan();
            taiKhoanSv.setTenDayDu("Hữu Khanh");
            taiKhoanSv.setTenDangNhap("khanh");
            taiKhoanSv.setMatKhau(passwordEncoder.encode("khanh123"));
            taiKhoanSv.setEmail("khanh@example.com");
            taiKhoanSv.setQuyen(TaiKhoan.Quyen.SinhVien);
            taiKhoanSv.setTrangthai(TaiKhoan.TrangThai.Mo);
            taiKhoanSv.setGioiTinh(TaiKhoan.GioiTinh.Nam);
            TaiKhoan mergedTaiKhoan = entityManager.merge(taiKhoanSv);
            SinhVien sinhVien = new SinhVien();
            sinhVien.setNamNhapHoc(Year.of(2019));
            sinhVien.setTaiKhoan(mergedTaiKhoan);
            sinhVienRepository.save(sinhVien);
        }
        if (!taiKhoanRepository.existsByQuyen(TaiKhoan.Quyen.GiangVien)) {
            // Tạo tài khoản mới với quyền là GiangVien
            TaiKhoan taiKhoanGv = new TaiKhoan();
            taiKhoanGv.setTenDayDu("La Chinh");
            taiKhoanGv.setTenDangNhap("chinh");
            taiKhoanGv.setMatKhau(passwordEncoder.encode("chinh123"));
            taiKhoanGv.setEmail("chinh@example.com");
            taiKhoanGv.setQuyen(TaiKhoan.Quyen.GiangVien);
            taiKhoanGv.setTrangthai(TaiKhoan.TrangThai.Mo);
            taiKhoanGv.setGioiTinh(TaiKhoan.GioiTinh.Nu);
            TaiKhoan mergedTaiKhoan = entityManager.merge(taiKhoanGv);
            GiangVien giangVien = new GiangVien();
            Optional<ChucDanh> chucDanhgv = chucDanhRepository.findById(Long.valueOf(1));
            giangVien.setChucDanh(chucDanhgv.get());
            giangVien.setTaiKhoan(mergedTaiKhoan);
            giangVienRepository.save(giangVien);
        }
    }
}
