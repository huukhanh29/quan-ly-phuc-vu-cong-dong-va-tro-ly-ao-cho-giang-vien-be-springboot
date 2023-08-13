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
    public static float calculateCosineSimilarity(String str1, String str2) {
        String[] words1 = str1.split(" ");
        String[] words2 = str2.split(" ");
        Map<String, Integer> wordFrequency1 = getWordFrequency(words1);
        Map<String, Integer> wordFrequency2 = getWordFrequency(words2);
        float dotProduct = 0.0f;
        float magnitude1 = 0.0f;
        float magnitude2 = 0.0f;
        for (Map.Entry<String, Integer> entry : wordFrequency1.entrySet()) {
            String word = entry.getKey();
            int frequency1 = entry.getValue();
            int frequency2 = wordFrequency2.getOrDefault(word, 0);
            dotProduct += frequency1 * frequency2;
            magnitude1 += frequency1 * frequency1;
        }
        for (Map.Entry<String, Integer> entry : wordFrequency2.entrySet()) {
            int frequency = entry.getValue();
            magnitude2 += frequency * frequency;
        }
        float magnitudeProduct = (float) Math.sqrt(magnitude1) * (float) Math.sqrt(magnitude2);
        if (magnitudeProduct == 0.0f) {
            return 0.0f;
        } else {
            return dotProduct / magnitudeProduct;
        }
    }

    public static Map<String, Integer> getWordFrequency(String[] words) {
        Map<String, Integer> frequencyMap = new HashMap<>();
        for (String word : words) {
            frequencyMap.put(word, frequencyMap.getOrDefault(word, 0) + 1);
        }
        return frequencyMap;
    }

    public static String createSlug(String s) {
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        return temp.replaceAll("[^\\p{ASCII}]", "");
    }

}
