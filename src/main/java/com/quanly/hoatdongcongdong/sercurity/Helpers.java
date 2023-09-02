package com.quanly.hoatdongcongdong.sercurity;


import com.quanly.hoatdongcongdong.entity.TaiKhoan;
import com.quanly.hoatdongcongdong.repository.TaiKhoanRepository;
import com.quanly.hoatdongcongdong.sercurity.jwt.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Helpers {
    public static float calculateSimilarity(String str1, String str2) {
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

    public static Map<String, Integer> getWordFrequency(String[] words) {
        Map<String, Integer> frequencyMap = new HashMap<>();
        for (String word : words) {
            frequencyMap.put(word, frequencyMap.getOrDefault(word, 0) + 1);
        }
        return frequencyMap;
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
