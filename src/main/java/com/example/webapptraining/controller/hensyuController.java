package com.example.webapptraining.controller;

import java.sql.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/mansion")
public class hensyuController {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public hensyuController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/hensyu")
    public String editData(@RequestParam("id") Integer id, Model model) {

        String sql = "SELECT ov.id, ov.name, ov.created_date, d.place, d.hometown, u.washing, u.now, u.memo FROM orverview ov LEFT JOIN details d ON ov.id = d.details_id LEFT JOIN update u ON ov.id = u.update_id WHERE ov.id = ?";
        
        Map<String, Object> data = jdbcTemplate.queryForMap(sql, id);
        model.addAttribute("data", data);
        return "hensyu";
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteRecord(@RequestParam("id") Integer id) {
        try {
            // detailsテーブルの削除
            String sqlDetails = "DELETE FROM details WHERE details_id = ?";
            jdbcTemplate.update(sqlDetails, id);

            // photosテーブルの削除
            String sqlPhotos = "DELETE FROM photos WHERE photos_id = ?";
            jdbcTemplate.update(sqlPhotos, id);

            // updateテーブルの削除
            String sqlUpdate = "DELETE FROM \"update\" WHERE update_id = ?";
            jdbcTemplate.update(sqlUpdate, id);

            // orverviewテーブルの削除
            String sqlOverview = "DELETE FROM orverview WHERE id = ?";
            jdbcTemplate.update(sqlOverview, id);

            // 削除が成功した場合
            return ResponseEntity.ok("削除成功");
        } catch (Exception e) {
            // エラー時の処理
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("削除失敗");
        }
    }

    @PostMapping("/update")
    public String updateData(
            @RequestParam("img") MultipartFile file,
            @RequestParam("id") Integer id,
            @RequestParam("name") String name,
            @RequestParam(value = "created_date", required = false) String createdDateStr,
            @RequestParam("place") String address,
            @RequestParam("hometown") String hometown,
            @RequestParam(value = "washing", required = false) String washingDateStr,
            @RequestParam("now") String status,
            @RequestParam("memo") String memo,
            Model model) {
                
        String sql = "SELECT ov.id, ov.name, ov.created_date, d.place, d.hometown, u.washing, u.now, u.memo FROM orverview ov LEFT JOIN details d ON ov.id = d.details_id LEFT JOIN update u ON ov.id = u.update_id WHERE ov.id = ?";
        Map<String, Object> data = jdbcTemplate.queryForMap(sql, id);
        model.addAttribute("data", data);
        try {
            // created_date のチェックと変換
            Date created_date = (createdDateStr != null && !createdDateStr.isEmpty()) ? Date.valueOf(createdDateStr)
                    : null;

            // washing のチェックと変換
            Date washing = (washingDateStr != null && !washingDateStr.isEmpty()) ? Date.valueOf(washingDateStr) : null;

            if (name == null || name.trim().isEmpty()) {
                model.addAttribute("error", "※名前が入力されていません(必須)");
                return "hensyu";
            }

            // データベースへの画像更新処理
            if (!file.isEmpty()) {
                byte[] imgBytes = file.getBytes(); // ファイルをバイト配列に変換

                // photosテーブルの既存レコードを更新
                String sqlPhotos = "UPDATE photos SET img = ? WHERE photos_id = ?";
                jdbcTemplate.update(sqlPhotos, imgBytes, id);
            }

            // orverviewテーブルのデータを更新
            String sqlOverview = "UPDATE orverview SET name = ?, created_date = ? WHERE id = ?";
            jdbcTemplate.update(sqlOverview, name, created_date, id);

            // detailsテーブルのデータを更新
            String sqlDetails = "UPDATE details SET place = ?, hometown = ? WHERE details_id = ?";
            jdbcTemplate.update(sqlDetails, address, hometown, id);

            // updateテーブルのデータを更新
            String sqlUpdate = "UPDATE \"update\" SET washing = ?, now = ?, memo = ? WHERE update_id = ?";
            jdbcTemplate.update(sqlUpdate, washing, status, memo, id);

            // 更新後のリダイレクト
            return "redirect:/mansion/home"; // 更新後のページリダイレクト
        } catch (Exception e) {
            e.printStackTrace();
            return "home";
        }
    }

}
