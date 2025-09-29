package org.example.backend_fivegivechill.controllers.Client;

import org.example.backend_fivegivechill.repository.SongRepository;
import org.example.backend_fivegivechill.response.DailyRavenueRespone;
import org.example.backend_fivegivechill.response.Response;
import org.example.backend_fivegivechill.services.DailyRavenueService;
import org.example.backend_fivegivechill.services.RevanueUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;



@RestController
@RequestMapping("/")
@CrossOrigin("*")
public class DailyRavenueClientController {
    //aaa
    @Autowired
    SongRepository songRepository;

    @Autowired
    DailyRavenueService dailyRavenueService;

    @Autowired
    RevanueUserService revanueUserService;

    // tinh lai nhung ngay chua co tinh
    @GetMapping("/daily-ravenue/recalculator-daily-ravenue")
    public ResponseEntity<Response> recalculatorDailyRavenue() {
        boolean recalculator = dailyRavenueService.recalculateMissingDailyRevenue();
        if (recalculator) {
            return ResponseEntity.ok(new Response(0, "Cập nhật thành công!", recalculator));
        } else return ResponseEntity.badRequest().body(new Response(1, "Cập nhật thất bại!", recalculator));
    }

    @GetMapping("/daily-ravenue/as")
    public ResponseEntity<Response> aaaa() {
        try {
            dailyRavenueService.calculateDailyRevenue();
            return ResponseEntity.ok(new Response(0, "Cập nhật thành công!", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null));
        }

    }

    @GetMapping("/daily-ravenue/ass")
    public ResponseEntity<Response> sas() {
        try {

            dailyRavenueService.calculateTodayRevenueForUser(12);
            return ResponseEntity.ok(new Response(0, "Cập nhật thành công!", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null));
        }

    }

    // tinh tien theo user hc tinh het tat ca
    // neu truyen id vao param thi lay theo id con khong truyen thi lay het ok
    @GetMapping("/daily-ravenue/calculator-daily-ravenue/{user_id}")
    public ResponseEntity<Response> calculatorDailyRavenue(
            @PathVariable(required = false) Integer user_id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            List<DailyRavenueRespone> dailyRavenueRespones = dailyRavenueService.findByUserIdEndFindAll(user_id, pageable);

            if (dailyRavenueRespones == null || dailyRavenueRespones.isEmpty()) {
                return ResponseEntity.badRequest().body(new Response(0, "khong co bai hat nao chon á", null));
            }

            return ResponseEntity.ok(new Response(0, "lụm rồi đó", dailyRavenueRespones));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, "get subscription not found", null));
        }
    }
}
