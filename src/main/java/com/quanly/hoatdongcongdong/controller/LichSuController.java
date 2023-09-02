package com.quanly.hoatdongcongdong.controller;
import com.quanly.hoatdongcongdong.entity.LichSu;
import com.quanly.hoatdongcongdong.payload.response.LichSuResponse;
import com.quanly.hoatdongcongdong.repository.LichSuRepository;
import com.quanly.hoatdongcongdong.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/api/lich-su")
@CrossOrigin(value = "*")
public class LichSuController {
    private final TaiKhoanService taiKhoanService;
    private final LichSuService lichSuService;
    @Autowired
    public LichSuController(
            LichSuService lichSuService,
            TaiKhoanService taiKhoanService) {
        this.lichSuService = lichSuService;
        this.taiKhoanService = taiKhoanService;
    }
    @GetMapping
    public Page<LichSuResponse> lichSu(HttpServletRequest httpServletRequest,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size,
                                       @RequestParam(defaultValue = "maLichSu") String sortBy,
                                       @RequestParam(defaultValue = "DESC") String sortDir,
                                       @RequestParam(required = false, defaultValue = "") String searchTerm) {
        Long userId = taiKhoanService.getCurrentUser(httpServletRequest).getMaTaiKhoan();
        return lichSuService.getLichSuByUserId(userId, page, size, sortBy, sortDir, searchTerm);
    }

    @GetMapping("/bieu-do-luot-hoi")
    public ResponseEntity<Map<String, Object>> getChart(@RequestParam("year") int year) {
        Map<String, Object> chartData = lichSuService.getChart(year);
        return ResponseEntity.ok().body(chartData);
    }

    @GetMapping("/danh-sach-nam-cua-lich-su")
    public ResponseEntity<List<Integer>> getYears() {
        List<Integer> years = lichSuService.getDistinctYears();
        return ResponseEntity.ok().body(years);
    }
}
