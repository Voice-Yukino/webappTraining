package com.example.webapptraining.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/mansion")
public class homeController {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public homeController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/home")
    public String search(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "now", required = false) String now,
            @RequestParam(value = "created_date_before", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdDateBefore,
            @RequestParam(value = "created_date_after", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdDateAfter,
            @RequestParam(value = "washing_before", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate washingBefore,
            @RequestParam(value = "washing_after", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate washingAfter,
            Model model) {
        List<Object> params = new ArrayList<>();
        List<String> conditions = new ArrayList<>();

        String sql = "SELECT photos_id, img FROM photos p " +
                "INNER JOIN orverview o ON p.photos_id = o.id " +
                "INNER JOIN \"update\" u ON o.id = u.update_id " +
                "WHERE 1=1";

        if (name != null && !name.trim().isEmpty()) {
            conditions.add("LOWER(o.name) LIKE LOWER(?)");
            params.add("%" + name.trim() + "%");
        }
        if (now != null && !now.trim().isEmpty()) {
            conditions.add("u.now = ?");
            params.add(now);
        }
        if (createdDateBefore != null) {
            conditions.add("o.created_date <= ?");
            params.add(createdDateBefore);
        }
        if (createdDateAfter != null) {
            conditions.add("o.created_date >= ?");
            params.add(createdDateAfter);
        }
        if (washingBefore != null) {
            conditions.add("u.washing <= ?");
            params.add(washingBefore);
        }
        if (washingAfter != null) {
            conditions.add("u.washing >= ?");
            params.add(washingAfter);
        }
        if (!conditions.isEmpty()) {
            sql += " AND " + String.join(" AND ", conditions);
        }
        sql += " ORDER BY p.photos_id ASC";

        List<Map<String, Object>> images = jdbcTemplate.query(
                sql, params.toArray(), (rs, rowNum) -> {
                    Map<String, Object> imageMap = new HashMap<>();
                    imageMap.put("id", rs.getInt("photos_id"));
                    imageMap.put("image", Base64.getEncoder().encodeToString(rs.getBytes("img")));
                    return imageMap;
                });

        model.addAttribute("images", images);
        model.addAttribute("name", name);
        model.addAttribute("now", now);
        model.addAttribute("created_date_before", createdDateBefore);
        model.addAttribute("created_date_after", createdDateAfter);
        model.addAttribute("washing_before", washingBefore);
        model.addAttribute("washing_after", washingAfter);

        // 単体テスト確認のため使用
        // for (Map hoge : images) {
        //     Object a = hoge.get("id");
        //     Integer b = (Integer) a;
        //     System.out.println(b);
        // }

        return "home";
    }

}
