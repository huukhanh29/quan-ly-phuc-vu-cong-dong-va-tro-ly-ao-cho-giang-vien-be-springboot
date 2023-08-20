package com.quanly.hoatdongcongdong.service;

import com.quanly.hoatdongcongdong.entity.GioTichLuy;
import com.quanly.hoatdongcongdong.repository.GioTichLuyRepository;
import com.quanly.hoatdongcongdong.repository.HoatDongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GioTichLuyService {

    private final GioTichLuyRepository gioTichLuyRepository;

    @Autowired
    public GioTichLuyService(GioTichLuyRepository gioTichLuyRepository) {
        this.gioTichLuyRepository = gioTichLuyRepository;
    }

    public GioTichLuy findByGiangVien_MaTaiKhoan(Long maTk) {
        return gioTichLuyRepository.findByGiangVien_MaTaiKhoan(maTk);
    }

    public List<GioTichLuy> findByNamHoc(String namHoc) {
        return gioTichLuyRepository.findByNamHoc(namHoc);
    }

    public GioTichLuy findByGiangVien_MaTaiKhoanAndNamHoc(Long nguoiDungId, String namHoc) {
        return gioTichLuyRepository.findByGiangVien_MaTaiKhoanAndNamHoc(nguoiDungId, namHoc);
    }

    public List<String> findDistinctNamHocByGiangVien(Long maTk) {
        return gioTichLuyRepository.findDistinctNamHocByGiangVien(maTk);
    }

    public GioTichLuy findByNamHocAndGiangVien_MaTaiKhoan(String namHoc, Long maTk) {
        return gioTichLuyRepository.findByNamHocAndGiangVien_MaTaiKhoan(namHoc, maTk);
    }
}


