package com.example.webapptraining.controller;

import java.time.LocalDate;
import java.util.Base64;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/mansion")
public class jyohoController {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public jyohoController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/profiel")
    public String profielData(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam("id") Integer id,
            @RequestParam(value = "now", required = false) String now,
            @RequestParam(value = "created_date_before", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdDateBefore,
            @RequestParam(value = "created_date_after", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdDateAfter,
            @RequestParam(value = "washing_before", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate washingBefore,
            @RequestParam(value = "washing_after", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate washingAfter,
            Model model) {

        String sql = "SELECT ov.id, ov.name, ov.created_date, d.place, d.hometown, u.washing, u.now, u.memo FROM orverview ov LEFT JOIN details d ON ov.id = d.details_id LEFT JOIN update u ON ov.id = u.update_id WHERE ov.id = ?";
        Map<String, Object> data = jdbcTemplate.queryForMap(sql, id);
        model.addAttribute("data", data);

        // 画像データを取得
        String sqlPhotos = "SELECT img FROM photos WHERE photos_id = ?";
        try {
            byte[] imgBytes = jdbcTemplate.queryForObject(sqlPhotos, byte[].class, id);
            String base64Image = Base64.getEncoder().encodeToString(imgBytes);
            model.addAttribute("profileImage", base64Image);
        } catch (EmptyResultDataAccessException e) {
            // 画像が見つからない場合の処理
            model.addAttribute("profileImage", null);
        }

        // 次のIDを取得
        String sqlNext = "SELECT MIN(id) FROM orverview WHERE id > ?";
        Integer nextId = jdbcTemplate.queryForObject(sqlNext, Integer.class, id);

        // 前のIDを取得
        String sqlPrev = "SELECT MAX(id) FROM orverview WHERE id < ?";
        Integer prevId = jdbcTemplate.queryForObject(sqlPrev, Integer.class, id);

        // 取得したIDを Thymeleaf に渡す
        model.addAttribute("nextId", nextId);
        model.addAttribute("prevId", prevId);
        model.addAttribute("hasNext", nextId != null);
        model.addAttribute("hasPrev", prevId != null);

        model.addAttribute("name", name);
        model.addAttribute("now", now);
        model.addAttribute("created_date_before", createdDateBefore);
        model.addAttribute("created_date_after", createdDateAfter);
        model.addAttribute("washing_before", washingBefore);
        model.addAttribute("washing_after", washingAfter);
        return "jyoho";
    }

}
