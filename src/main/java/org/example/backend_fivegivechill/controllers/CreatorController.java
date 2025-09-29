package org.example.backend_fivegivechill.controllers;

import org.example.backend_fivegivechill.beans.LineChartBean;
import org.example.backend_fivegivechill.entity.UserEntity;
import org.example.backend_fivegivechill.repository.UserRepository;
import org.example.backend_fivegivechill.response.AdminRespone;
import org.example.backend_fivegivechill.response.Response;
import org.example.backend_fivegivechill.services.AdminService;
import org.example.backend_fivegivechill.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("/creator")
public class CreatorController {
    @Autowired
    private AdminService adminService;

    @Autowired
    private UserRepository userRepository;
    //hello
    @PostMapping("/dashboard")
    public ResponseEntity<Response> getAdminService(@RequestBody LineChartBean lineChartBean) {
        try{
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            Optional<UserEntity> userOpt = userRepository.findByEmail(email);

            if (userOpt.isEmpty()) {
                return null;
            }

            if(lineChartBean.getFirst() == null){
                lineChartBean.setFirst("");
            }
            if(lineChartBean.getLast() == null){
                lineChartBean.setLast("");
            }
            AdminRespone adminRespone = new AdminRespone();
            adminRespone.setStatisticsByMonth(adminService.statisticsByMonth());
            adminRespone.setStatisticsByYear(adminService.statisticsByYear());
            adminRespone.setStatisticsSumUsers(adminService.statisticsSumUsersCreator(userOpt.get().getId()));
            adminRespone.setStatisticsSumSongs(adminService.statisticsSumSongsCreator(userOpt.get().getId()));
            adminRespone.setStatisticsTop5Songs(adminService.statisticsTop5SongsCreator(userOpt.get().getId()));
            adminRespone.setStatisticsLineChart(adminService.lineChart(lineChartBean.getFirst(), lineChartBean.getLast()));

            Response response = new Response(0, "success", adminRespone);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null));
        }
    }
}
