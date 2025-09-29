package org.example.backend_fivegivechill.controllers.Client;

import org.example.backend_fivegivechill.beans.ReportBean;
import org.example.backend_fivegivechill.entity.ReportEntity;
import org.example.backend_fivegivechill.response.Response;
import org.example.backend_fivegivechill.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/client/report")
@CrossOrigin("*")
public class ReportClientController {

    @Autowired
    private ReportService reportService;



    @PostMapping("/add")
    public ResponseEntity<Response> addReport(@RequestBody ReportBean reportBean) {
        ReportEntity report = reportService.addReport(reportBean);
        if (report != null) {
            return ResponseEntity.ok(new Response(0, "Success", reportService.mapToResponse(report)));
        } else {
            return ResponseEntity.badRequest().body(new Response(1, "Failed", null));
        }
    }
}

//húhduhsuhdushshd
