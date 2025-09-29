package org.example.backend_fivegivechill.controllers.Client;

import jakarta.servlet.http.HttpServletRequest;
import org.example.backend_fivegivechill.entity.UserEntity;
import org.example.backend_fivegivechill.repository.UserRepository;
import org.example.backend_fivegivechill.response.CountListenResponse;
import org.example.backend_fivegivechill.response.Response;
import org.example.backend_fivegivechill.services.CountListenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@CrossOrigin("*")
public class CountListenCLientController {

    @Autowired
    CountListenService countListenService;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/count-listen/calculator-count-listen") //aaaaaaaaaaaaaaaaaaaaaaaaaaa
    public ResponseEntity<Response> sdsd() {
        try {
            countListenService.calculatorCountListenEveryday();
            return ResponseEntity.ok(new Response(1, "Cập nhật Thành công!", null));
        }catch (Exception e){
           return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null));
        }
    } //aaaaaaaaaaaaaaaaaaaaaaaaaaa


    @PostMapping("/addCountListen")
    public ResponseEntity<Response> addCountListen(@RequestBody CountListenResponse countListenResponse) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            UserEntity user = null;
            if (authentication != null && authentication.isAuthenticated()
                    && !authentication.getPrincipal().equals("anonymousUser")) {
                String email = authentication.getName();
                user = userRepository.findByEmail(email).orElse(null);
            }

            String message = countListenService.countListen(countListenResponse.getSongId(), countListenResponse.getFingerprint(), user);
            return ResponseEntity.ok(new Response(0, message, null));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null));
        }
    }



}
