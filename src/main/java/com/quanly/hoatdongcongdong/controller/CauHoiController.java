package com.quanly.hoatdongcongdong.controller;

import com.quanly.hoatdongcongdong.entity.*;
import com.quanly.hoatdongcongdong.exception.ResourceNotFoundException;
import com.quanly.hoatdongcongdong.payload.request.CauHoiRequest;
import com.quanly.hoatdongcongdong.payload.response.MessageResponse;
import com.quanly.hoatdongcongdong.repository.FilesStorageService;
import com.quanly.hoatdongcongdong.service.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.quanly.hoatdongcongdong.sercurity.Helpers.*;

@RestController
@RequestMapping("/api/cau-hoi")
@CrossOrigin(value = "*")
public class CauHoiController {

    private final CauHoiService cauHoiService;
    private final PhanHoiService phanHoiService;
    private final LichSuService lichSuService;
    private final SinhVienService sinhVienService;
    private final TaiKhoanService taiKhoanService;
    @Autowired
    private FilesStorageService storageService;

    @Autowired
    public CauHoiController(CauHoiService cauHoiService, PhanHoiService phanHoiService,
                            LichSuService lichSuService, SinhVienService sinhVienService,
                            TaiKhoanService taiKhoanService) {
        this.cauHoiService = cauHoiService;
        this.phanHoiService = phanHoiService;
        this.lichSuService = lichSuService;
        this.sinhVienService = sinhVienService;
        this.taiKhoanService = taiKhoanService;
    }

    @PostMapping("/dat-cau-hoi")
    public ResponseEntity<?> getAnswer(@RequestBody CauHoiRequest request,
                                       HttpServletRequest httpServletRequest) {
        String question = request.getCauHoi().toLowerCase();

        List<CauHoi> cauHoiList = cauHoiService.findAllNoPage();

        CauHoi bestMatch = null;
        float maxSimilarity = 0.5f;

        for (CauHoi cauHoi : cauHoiList) {
            String query = cauHoi.getCauHoi().toLowerCase();
            float similarity = calculateSimilarity(createSlug(question), createSlug(query));

            if (similarity > maxSimilarity) {
                bestMatch = cauHoi;
                maxSimilarity = similarity;
            }
        }

        if (bestMatch != null) {
            TaiKhoan currentUser = taiKhoanService.getCurrentUser(httpServletRequest);
            Optional<SinhVien> sinhVien = sinhVienService.findById(currentUser.getMaTaiKhoan());

            if (sinhVien.isPresent()) {
                LichSu lichSu = new LichSu();
                lichSu.setCauHoi(bestMatch);
                lichSu.setSinhVien(sinhVien.get());
                lichSuService.saveLichSu(lichSu);
                return ResponseEntity.ok(bestMatch);
            } else {
                return new ResponseEntity<>(new MessageResponse("sinhvien-notfound"), HttpStatus.NOT_FOUND);
            }
        } else {
            return ResponseEntity.ok(new MessageResponse("unknown"));
        }
    }


    @PostMapping("/them-moi")
    public ResponseEntity<?> createCauHoi(@RequestBody CauHoi cauHoi) {
        if (cauHoiService.findByCauHoi(cauHoi.getCauHoi()) != null) {
            return new ResponseEntity<>(new MessageResponse("cauhoi-exist"), HttpStatus.OK);
        } else {
            cauHoiService.saveCauHoi(cauHoi);
            return ResponseEntity.ok(cauHoi);
        }
    }

    @GetMapping("/lay-danh-sach")
    public Page<CauHoi> getAllCauHoi(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ngayTao") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir,
            @RequestParam(required = false, defaultValue = "") String searchTerm
    ) {
        return cauHoiService.getAllCauHoi(page, size, sortBy, sortDir, searchTerm);
    }

    @GetMapping("/{ma}")
    public ResponseEntity<?> getCauHoiById(@PathVariable(value = "ma") Long cauHoiId) {
        Optional<CauHoi> cauHoi = cauHoiService.getCauHoiById(cauHoiId);
        if (cauHoi.isPresent()) {
            return ResponseEntity.ok(cauHoi.get());
        } else {
            return new ResponseEntity<>(new MessageResponse("cauhoi-notfound"), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/cap-nhat/{ma}")
    public ResponseEntity<?> updateCauHoi(@PathVariable(value = "ma") Long cauHoiId,
                                          @RequestBody CauHoi cauHoiDetails) {

        CauHoi updatedCauHoi = cauHoiService.updateCauHoi(cauHoiId, cauHoiDetails);
        return ResponseEntity.ok(updatedCauHoi);

    }

    @DeleteMapping("/xoa/{ma}")
    public ResponseEntity<?> deleteCauHoi(@PathVariable(value = "ma") Long cauHoiId) {

        PhanHoi feedback = phanHoiService.findFirstByCauHoi_MaCauHoi(cauHoiId);
        LichSu historyEntity = lichSuService.findFirstByCauHoi_MaCauHoi(cauHoiId);
        if (feedback != null || historyEntity != null) {
            return new ResponseEntity<>(new MessageResponse("cant-delete"), HttpStatus.OK);
        }
        cauHoiService.deleteCauHoiById(cauHoiId);
        return ResponseEntity.ok(new MessageResponse("đã xóa"));

    }

    @GetMapping("/tong-cau-hoi")
    public Long countCauHoi() {
        return cauHoiService.countCauHoi();
    }

    @PostMapping("/them-file")
    public ResponseEntity<?> uploadWordFile(@RequestParam("file") MultipartFile file) {
        try {
            XWPFDocument document = new XWPFDocument(file.getInputStream());
            List<CauHoi> cauHoiList = new ArrayList<>();
            int paragraphCount = 0; // Bắt đầu từ đoạn thứ 0

            for (XWPFParagraph paragraph : document.getParagraphs()) {
                paragraphCount++; // Tăng biến đếm cho mỗi đoạn được xử lý
                String text = paragraph.getText();
                System.out.println("Paragraph content: " + text);
                String[] lines = text.split("\\n");

                // Kiểm tra số lượng dòng và định dạng của từng dòng
                if (lines.length >= 2 &&
                        lines[0].matches("^\\d+\\.\\s*Từ\\s*khóa:\\s*.*") &&
                        lines[1].matches("^Câu\\s*trả\\s*lời:\\s*.*")) {

                    String tuKhoa = lines[0].replaceFirst("^\\d+\\.\\s*Từ\\s*khóa:\\s*", "").trim();
                    String cauTraLoi = lines[1].replaceFirst("Câu\\s*trả\\s*lời:\\s*", "").trim();

                    System.out.println("Keyword: " + tuKhoa);
                    System.out.println("Answer: " + cauTraLoi);

                    // Kiểm tra câu hỏi đã tồn tại trong cơ sở dữ liệu hay chưa
                    if (!cauHoiService.existsByCauHoi(tuKhoa)) {
                        CauHoi cauHoi = new CauHoi();
                        cauHoi.setCauHoi(tuKhoa);
                        cauHoi.setTraLoi(cauTraLoi);
                        cauHoiList.add(cauHoi);
                    } else {
                        // Câu hỏi đã tồn tại, thực hiện xử lý tùy ý (ví dụ: ghi log)
                        return new ResponseEntity<>(new MessageResponse("Exist: " + paragraphCount), HttpStatus.OK);
                    }
                } else {
                    // Trả về thông báo lỗi với số thứ tự của đoạn văn bị sai
                    return new ResponseEntity<>(new MessageResponse("Error: " + paragraphCount), HttpStatus.OK);
                }
            }

            // Lưu danh sách câu hỏi mới vào cơ sở dữ liệu
            cauHoiService.saveAll(cauHoiList);
            return new ResponseEntity<>(new MessageResponse("ok"), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new MessageResponse("error"), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/file-mau/download")
    public ResponseEntity<?> downloadFile() {
        try {
            String fileName = "mau-tep-them-bo-du-lieu.docx";

            Resource file = storageService.load(fileName); // Giả định storageService có một hàm load() để lấy file dựa trên tên
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                    .body(file);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(null);
        }

    }
}


