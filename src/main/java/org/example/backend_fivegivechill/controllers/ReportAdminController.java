package org.example.backend_fivegivechill.controllers;

import jakarta.validation.Valid;
import org.example.backend_fivegivechill.beans.SongBean;
import org.example.backend_fivegivechill.entity.ReportEntity;
import org.example.backend_fivegivechill.entity.SongEntity;
import org.example.backend_fivegivechill.entity.UserEntity;
import org.example.backend_fivegivechill.repository.ReportRepository;
import org.example.backend_fivegivechill.repository.SongRepository;
import org.example.backend_fivegivechill.repository.UserRepository;
import org.example.backend_fivegivechill.response.ReportResponse;
import org.example.backend_fivegivechill.response.Response;
import org.example.backend_fivegivechill.response.TestURL;
import org.example.backend_fivegivechill.services.ReportService;
import org.example.backend_fivegivechill.services.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/report")
@CrossOrigin("*")
public class ReportAdminController {

    @Autowired
    private ReportService reportService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    SongService songService;
    @Autowired
    private SongRepository songRepository;

    // Lấy tất cả báo cáo
    @GetMapping("/all")
    public ResponseEntity<Response> getAllReports() {
        try {
            List<ReportResponse> responses = reportService.getAllReportsForAdmin();
            return ResponseEntity.ok(new Response(0, "Success!", responses));
        } catch (Exception e) {
            e.printStackTrace(); // In lỗi ra console, có thể thay bằng logger nếu cần
            return ResponseEntity
                    .badRequest()
                    .body(new Response(1, "Đã xảy ra lỗi khi lấy danh sách báo cáo", null));
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<Response> getReportByID(@PathVariable  int id) {
        try {
            ReportEntity reportEntity = reportRepository.findById(id).orElse(null);
            if(reportEntity == null) {
                return ResponseEntity
                        .badRequest()
                        .body(new Response(1, "Đã xảy ra lỗi khi lấy danh sách báo cáo", null));
            }
            ReportResponse reportResponse = new ReportResponse(reportEntity.getId(),
                    reportEntity.getSong().getId(), reportEntity.getSong().getName(), reportEntity.getSong().getAvatar(),
                    reportEntity.getSong().isVipSong(), reportEntity.getSong().getStatus(), reportEntity.getContent());
            return ResponseEntity.ok(new Response(0, "Success!", reportResponse));
        } catch (Exception e) {
            e.printStackTrace(); // In lỗi ra console, có thể thay bằng logger nếu cần
            return ResponseEntity
                    .badRequest()
                    .body(new Response(1, "Đã xảy ra lỗi khi lấy danh sách báo cáo", null));
        }
    }
    // Lấy báo cáo theo ID
//    @GetMapping("/{id}")
//    public ResponseEntity<Response> getReportById(@PathVariable int id) {
//        ReportEntity report = reportService.getReportById(id);
//        if (report != null) {
//            return ResponseEntity.ok(new Response(0, "Fetched report", reportService.mapToResponse(report)));
//        } else {
//            return ResponseEntity.badRequest().body(new Response(1, "Report not found", null));
//        }
//    }

    @DeleteMapping("/delete-songReport/{reportId}")
    public ResponseEntity<Response> deleteSongByReport(@PathVariable int reportId) {
//        UserEntity userEntity = userRepository.findUserBySongId(reportEntity.getSong().getId());
        boolean success = reportService.deleteSongOfReport(reportId);
        if (success) {
//            messagingTemplate.convertAndSend("/topic/notifications/creator" + userEntity.getId(), reportEntity.getContent());
            return ResponseEntity.ok(new Response(0, "Song deleted and email sent if applicable", null));
        } else {
            return ResponseEntity.badRequest().body(new Response(1, "Failed to delete song or report not found", null));
        }
    }

    // Xac nhan vi pham
    @PutMapping("/confrimReport/{reportID}")
    public ResponseEntity<Response> confirmReport(@PathVariable int reportID, @Valid @RequestBody SongBean songBean, Errors errors) {
        System.out.println("confim");
        ReportEntity reportEntity = reportRepository.findById(reportID).orElse(null);

        if (errors.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            errors.getFieldErrors().forEach(fieldError -> {
                errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
            });
            return ResponseEntity.badRequest().body(new Response(1, "Validation failed", errorMap));
        }



        try {
            SongEntity updatedSong = songService.updateSong(reportEntity.getSong().getId(), songBean);
            if(songBean.getStatus() == 3){
                System.out.println("delete");
                reportRepository.deleteReportNotLike(reportID);
            }

            if (updatedSong == null) {
                return ResponseEntity.badRequest().body(new Response(1, "Song not found", null));
            }
            return ResponseEntity.ok(new Response(0, "Success!", updatedSong));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null));
        }
    }
}
