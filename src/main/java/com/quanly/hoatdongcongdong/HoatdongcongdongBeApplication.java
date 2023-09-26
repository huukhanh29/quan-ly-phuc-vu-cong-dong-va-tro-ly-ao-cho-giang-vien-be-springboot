package com.quanly.hoatdongcongdong;

import com.quanly.hoatdongcongdong.entity.*;
import com.quanly.hoatdongcongdong.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
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
            adminAccount.setTrangThai(TaiKhoan.TrangThai.Mo);
            adminAccount.setGioiTinh(TaiKhoan.GioiTinh.Nam);
            taiKhoanRepository.save(adminAccount);
        }

    }
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
    @Bean
    public CommandLineRunner initializeDb(JdbcTemplate jdbcTemplate) {
        return args -> {
            String dropFunctionSql = "DROP FUNCTION IF EXISTS CALCULATE_SIMILARITY;";
            jdbcTemplate.execute(dropFunctionSql);

            String createFunctionSql = "CREATE FUNCTION CALCULATE_SIMILARITY(str1 TEXT, str2 TEXT) RETURNS FLOAT " +
                    "DETERMINISTIC " +
                    "BEGIN " +
                    "   DECLARE tmp TEXT; " +
                    "   DECLARE s1 TEXT; " +
                    "   DECLARE s2 TEXT; " +
                    "   DECLARE words1_len INT; " +
                    "   DECLARE words2_len INT; " +
                    "   DECLARE common_count INT DEFAULT 0; " +
                    "   DECLARE i INT DEFAULT 1; " +
                    "   DECLARE j INT DEFAULT 1; " +
                    "   DECLARE word1 VARCHAR(255); " +
                    "   DECLARE word2 VARCHAR(255); " +
                    "   IF LENGTH(str1) > LENGTH(str2) THEN " +
                    "     SET s1 = str2; " +
                    "     SET s2 = str1; " +
                    "   ELSE " +
                    "     SET s1 = str1; " +
                    "     SET s2 = str2; " +
                    "   END IF; " +
                    "   SET words1_len = LENGTH(s1) - LENGTH(REPLACE(s1, ' ', '')) + 1; " +
                    "   SET words2_len = LENGTH(s2) - LENGTH(REPLACE(s2, ' ', '')) + 1; " +
                    "   WHILE i <= words1_len DO " +
                    "     SET word1 = SUBSTRING_INDEX(SUBSTRING_INDEX(s1, ' ', i), ' ', -1); " +
                    "     SET j = 1; " +
                    "     WHILE j <= words2_len DO " +
                    "       SET word2 = SUBSTRING_INDEX(SUBSTRING_INDEX(s2, ' ', j), ' ', -1); " +
                    "       IF word1 = word2 THEN " +
                    "         SET common_count = common_count + 1; " +
                    "       END IF; " +
                    "       SET j = j + 1; " +
                    "     END WHILE; " +
                    "     SET i = i + 1; " +
                    "   END WHILE; " +
                    "   RETURN common_count / words2_len; " +
                    "END;";

            jdbcTemplate.execute(createFunctionSql);
        };
    }

}
