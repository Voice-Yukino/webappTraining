package com.example.webapptraining.controller;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/mansion")
public class tourokuController {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public tourokuController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/nyukyo")
    public String nyukyoView(Model model) {

        return "touroku";
    }

    @PostMapping("/register")
    public String registerData(
            @RequestParam("img") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam(value = "created_date", required = false) String createdDateStr,
            @RequestParam("place") String address,
            @RequestParam("hometown") String hometown,
            @RequestParam("now") String status,
            @RequestParam("memo") String memo,
            Model model)
            throws Exception {
        // created_date のチェックと変換
        Date created_date = (createdDateStr != null && !createdDateStr.isEmpty()) ? Date.valueOf(createdDateStr) : null;

        // 必須入力のバリデーション
        List<String> errors = new ArrayList<>();

        if (name == null || name.trim().isEmpty()) {
            errors.add("※名前が入力されていません(必須)");
        }

        if (file == null || file.isEmpty()) {
            errors.add("※画像が選択されていません(必須)");
        }

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            return "touroku";
        }
        
        // orverviewテーブルにデータを挿入し、生成されたidを取得
        String sqlOverview = "INSERT INTO orverview (name, created_date) VALUES (?, ?) RETURNING id";
        Integer generatedId = jdbcTemplate.queryForObject(sqlOverview, Integer.class, name, created_date);

        // detailsテーブルにデータを挿入
        String sqlDetails = "INSERT INTO details (details_id, place, hometown) VALUES (?, ?, ?)";
        jdbcTemplate.update(sqlDetails, generatedId, address, hometown);

        // updateテーブルにデータを挿入
        String sqlUpdate = "INSERT INTO update (update_id, washing, now, memo) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sqlUpdate, generatedId, null, status, memo); // washingをNULLに設定

        // 画像をphotosテーブルに挿入
        if (!file.isEmpty()) {
            byte[] imgBytes = file.getBytes(); // ファイルをバイト配列に変換
            String sqlPhotos = "INSERT INTO photos (photos_id, img) VALUES (?, ?)";
            jdbcTemplate.update(sqlPhotos, generatedId, imgBytes);
        }
        
        return "redirect:/mansion/home"; // 登録後のページリダイレクト
    }
}
