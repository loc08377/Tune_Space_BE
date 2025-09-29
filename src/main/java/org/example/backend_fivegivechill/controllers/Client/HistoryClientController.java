package org.example.backend_fivegivechill.controllers.Client;

import org.example.backend_fivegivechill.beans.HistoryBean;
import org.example.backend_fivegivechill.response.DeleteHistoryResponse;
import org.example.backend_fivegivechill.entity.HistoryEntity;
import org.example.backend_fivegivechill.response.HistoryResponse;
import org.example.backend_fivegivechill.response.Response;
import org.example.backend_fivegivechill.services.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/")
@CrossOrigin("*")
public class HistoryClientController {

    @Autowired
    HistoryService historyService;

    @GetMapping("/user/history/{id}")
    public ResponseEntity<Response> getHistory(@PathVariable int id) {
        try {
            List<HistoryResponse> historyResponses = historyService.getHistoryByUserId(id);
            if (historyResponses.isEmpty()) {
                return ResponseEntity.ok(new Response(0, "Không tìm thấy lịch sử cho người dùng có ID: " + id, false));
            }
            return ResponseEntity.ok(new Response(0, "Lấy lịch sử thành công", historyResponses));
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new Response(1, "Lấy lịch sử thất bại: " + e.getMessage(), null));
        }
    }

//    @PostMapping("/user/history/delete/{id}")
//    public ResponseEntity<Response> deleteHistory(@PathVariable int id, @RequestBody DeleteHistoryResponse deleteHistoryResponse) {
//        try {
//            boolean check = historyService.deleteHistory(id, deleteHistoryResponse.getHistoryIds());
//            if (check) {
//                Response response = new Response(0, "Xóa lịch sử thành công!", true);
//                return ResponseEntity.ok(response);
//            } else {
//                Response response = new Response(0, "Xóa lịch sử không thành công!", false);
//                return ResponseEntity.ok(response);
//            }
//        } catch (Exception e) {
//            return ResponseEntity
//                    .badRequest()
//                    .body(new Response(1, "Xóa lịch sử thất bại: " + e.getMessage(), null));
//        }
//    }

    @PostMapping("/user/history/delete/{id}")
    public ResponseEntity<Response> deleteHistory(
            @PathVariable int id,
            @RequestBody Map<String, List<Integer>> request) {
        try {
            List<Integer> songIds = request.get("songIds");
            boolean check = historyService.deleteHistory(id, songIds);
            System.out.println("SongIdssssssssss: " + songIds);
            System.out.println("UserIdddddddddddddd: " + id);

            if (check) {
                return ResponseEntity.ok(new Response(0, "Xóa lịch sử thành công!", true));
            } else {
                return ResponseEntity.ok(new Response(0, "Xóa lịch sử không thành công!", false));
            }
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new Response(1, "Xóa lịch sử thất bại: " + e.getMessage(), null));
        }
    }


    @PostMapping("/user/history")
    public ResponseEntity<Response> saveHistory(@RequestBody HistoryBean historyBean) {
        try {
            // Nếu chưa tồn tại → Thêm mới
            int isSaved = historyService.saveHistory(historyBean);
            if (isSaved == 1) {
                return ResponseEntity.ok(new Response(0, "Lưu lịch sử nghe thành công!", true));
            } else if (isSaved == 2) {
                return ResponseEntity.ok(new Response(0, "Cập nhật thời gian thành công!", true));
            } else if (isSaved == 0) {
                return ResponseEntity
                        .badRequest()
                        .body(new Response(1, "Không tìm thấy người dùng hoặc bài hát", null));
            } else {
                return ResponseEntity
                        .badRequest()
                        .body(new Response(1, "Lỗi trong quá trình xử lý!", null));
            }
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new Response(1, "Lỗi: " + e.getMessage(), null));
        }
    }

}
