package com.quanly.hoatdongcongdong.sercurity;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.quanly.hoatdongcongdong.entity.TaiKhoan;
import com.quanly.hoatdongcongdong.repository.TaiKhoanRepository;
import com.quanly.hoatdongcongdong.sercurity.jwt.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Helpers {
    public static float calculateSimilarity(String str1, String str2) {
        // Nếu str1 dài hơn str2, đổi chỗ chúng
        if(str1.length() > str2.length()) {
            String temp = str1;
            str1 = str2;
            str2 = temp;
        }

        String[] words1 = str1.split(" ");
        String[] words2 = str2.split(" ");
        int matches = 0;
        for (String word1 : words1) {
            if (Arrays.stream(words2).anyMatch(word1::equals)) {
                matches++;
            }
        }
        return (float) matches / words2.length;
    }


    public static ResponseEntity<String> createErrorResponse(String errorMessage, HttpStatus httpStatus) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            // Chuyển thông báo lỗi thành JSON
            String errorJson = objectMapper.writeValueAsString(errorMessage);
            // Trả về JSON và mã HTTP tùy chỉnh
            return new ResponseEntity<>(errorJson, httpStatus);
        } catch (Exception e) {
            // Xử lý các exception khác nếu cần
            return new ResponseEntity<>("Lỗi xử lý JSON", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public static String createSlug(String string) {
        String[] search = {
                "(à|á|ạ|ả|ã|â|ầ|ấ|ậ|ẩ|ẫ|ă|ằ|ắ|ặ|ẳ|ẵ)",
                "(è|é|ẹ|ẻ|ẽ|ê|ề|ế|ệ|ể|ễ)",
                "(ì|í|ị|ỉ|ĩ)",
                "(ò|ó|ọ|ỏ|õ|ô|ồ|ố|ộ|ổ|ỗ|ơ|ờ|ớ|ợ|ở|ỡ)",
                "(ù|ú|ụ|ủ|ũ|ư|ừ|ứ|ự|ử|ữ)",
                "(ỳ|ý|ỵ|ỷ|ỹ)",
                "(đ)",
                "(À|Á|Ạ|Ả|Ã|Â|Ầ|Ấ|Ậ|Ẩ|Ẫ|Ă|Ằ|Ắ|Ặ|Ẳ|Ẵ)",
                "(È|É|Ẹ|Ẻ|Ẽ|Ê|Ề|Ế|Ệ|Ể|Ễ)",
                "(Ì|Í|Ị|Ỉ|Ĩ)",
                "(Ò|Ó|Ọ|Ỏ|Õ|Ô|Ồ|Ố|Ộ|Ổ|Ỗ|Ơ|Ờ|Ớ|Ợ|Ở|Ỡ)",
                "(Ù|Ú|Ụ|Ủ|Ũ|Ư|Ừ|Ứ|Ự|Ử|Ữ)",
                "(Ỳ|Ý|Ỵ|Ỷ|Ỹ)",
                "(Đ)",
                "[^a-zA-Z0-9\\-_]"
        };

        String[] replace = {
                "a",
                "e",
                "i",
                "o",
                "u",
                "y",
                "d",
                "A",
                "E",
                "I",
                "O",
                "U",
                "Y",
                "D",
                "-"
        };

        String temp = string;

        for (int i = 0; i < search.length; i++) {
            temp = temp.replaceAll(search[i], replace[i]);
        }

        temp = temp.replaceAll("(-)+", " ");
        temp = temp.toLowerCase();

        // Remove diacritics using Normalizer
        temp = Normalizer.normalize(temp, Normalizer.Form.NFD);
        temp = temp.replaceAll("[^\\p{ASCII}]", "");

        return temp;
    }

}
