package com.quanly.hoatdongcongdong.service;

import com.quanly.hoatdongcongdong.entity.ChucDanh;
import com.quanly.hoatdongcongdong.repository.ChucDanhRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChucDanhService {

    private final ChucDanhRepository chucDanhRepository;

    @Autowired
    public ChucDanhService(ChucDanhRepository chucDanhRepository) {
        this.chucDanhRepository = chucDanhRepository;
    }

    public Optional<ChucDanh> findById(Long maChucDanh) {
        return chucDanhRepository.findById(maChucDanh);

    }
    public List<ChucDanh> findAll() {
        return chucDanhRepository.findAll();
    }
}

