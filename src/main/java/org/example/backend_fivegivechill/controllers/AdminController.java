package org.example.backend_fivegivechill.controllers;

import org.example.backend_fivegivechill.beans.LineChartBean;
import org.example.backend_fivegivechill.response.AdminRespone;
import org.example.backend_fivegivechill.response.Response;
import org.example.backend_fivegivechill.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;
//hello
    @PostMapping("/dashboard")
    public ResponseEntity<Response> getAdminService(@RequestBody LineChartBean lineChartBean,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "6") int size) {
        try{
            Pageable pageable = PageRequest.of(page, size);
            Page<Object[]> listSubPackUser = adminService.listSubPackUser(pageable);
            if(lineChartBean.getFirst() == null){
                lineChartBean.setFirst("");
            }
            if(lineChartBean.getLast() == null){
                lineChartBean.setLast("");
            }
            AdminRespone adminRespone = new AdminRespone();
            adminRespone.setStatisticsByMonth(adminService.statisticsByMonth());
            adminRespone.setStatisticsByYear(adminService.statisticsByYear());
            adminRespone.setStatisticsSumUsers(adminService.statisticsSumUsers());
            adminRespone.setStatisticsSumCreator(adminService.statisticsSumCreator());
            adminRespone.setStatisticsSumSongs(adminService.statisticsSumSongs());
            adminRespone.setStatisticsTop5Songs(adminService.statisticsTop5Songs());
            adminRespone.setStatisticsLineChart(adminService.lineChart(lineChartBean.getFirst(), lineChartBean.getLast()));
            adminRespone.setListSubPackUser(listSubPackUser.getContent());

            Response response = new Response(0, "success", adminRespone);
            response.setTotalPages(listSubPackUser.getTotalPages());
            response.setTotalElements(page);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null));
        }
    }
}
