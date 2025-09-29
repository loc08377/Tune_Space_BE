package org.example.backend_fivegivechill.controllers.Client;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.backend_fivegivechill.beans.SubscriptionUserBean;
import org.example.backend_fivegivechill.entity.UserEntity;
import org.example.backend_fivegivechill.repository.UserRepository;
import org.example.backend_fivegivechill.response.Response;
import org.example.backend_fivegivechill.response.SubscriptionStatusResponse;
import org.example.backend_fivegivechill.response.SubscriptionUserResponse;
import org.example.backend_fivegivechill.services.SubscriptionPackage_userService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@CrossOrigin("*")

@RequestMapping("/")
public class SubscriptionPackage_userClientController {
    private final SubscriptionPackage_userService subscriptionPackageUserService;
    private final HttpServletRequest request;

    @Autowired
    UserRepository userRepository;

    // Endpoint hiện tại: Trả về danh sách gói
    @GetMapping("/subscription-packages/user/{id}")
    public ResponseEntity<Response> getSubscriptionPackageByIdUser(@PathVariable int id) {
        try {
            List<SubscriptionUserResponse> subscriptions = subscriptionPackageUserService.findActiveByUserId(id);

            if (subscriptions.isEmpty()) {
                return ResponseEntity.ok(new Response(0, "Người dùng chưa đăng ký gói nào còn hiệu lực", List.of()));
            }

            return ResponseEntity.ok(new Response(0, "Danh sách gói còn hiệu lực", subscriptions));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null));
        }
    }

    @GetMapping("/subscription-packages/active/user/{id}")
    public ResponseEntity<Response> getActiveSubscriptionsByUser(@PathVariable int id) {
        try {
            List<SubscriptionUserResponse> subscriptions = subscriptionPackageUserService.getActiveSubscriptions(id);
            return ResponseEntity.ok()
                    .body(new Response(0, "Success", subscriptions));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new Response(1, e.getMessage(), null));
        }
    }

    @GetMapping("/subscription-packages/loc/user/{id}")
    public ResponseEntity<Response> aaaaa(@PathVariable int id) {
        try {
            List<SubscriptionUserResponse> subscriptions = subscriptionPackageUserService.getAllSubscriptionsByUserId(id);
            return ResponseEntity.ok()
                    .body(new Response(0, "Success", subscriptions));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new Response(1, e.getMessage(), null));
        }
    }

    @GetMapping("/subscription-packages")
    public ResponseEntity<Response> getSub() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            Optional<UserEntity> userOpt = userRepository.findByEmail(email);
            List<SubscriptionUserResponse> subscriptions = subscriptionPackageUserService.getAllSubscriptionsByUserId(userOpt.get().getId());
            return ResponseEntity.ok()
                    .body(new Response(0, "Success", subscriptions));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new Response(1, e.getMessage(), null));
        }
    }

    // Endpoint mới: Trả về trạng thái VIP và số ngày còn lại
    @GetMapping("/subscription-packages/status/user/{id}")
    public ResponseEntity<Response> getSubscriptionStatus(@PathVariable int id) {
        try {
            SubscriptionStatusResponse status = subscriptionPackageUserService.getSubscriptionStatus(id);
            return ResponseEntity.ok(new Response(0, "Trạng thái đăng ký", status));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null));
        }
    }

    @PutMapping("/subscription-packages/save-payment")
    public ResponseEntity<Response> savePayment(@RequestBody SubscriptionUserBean subscriptionUserBean) {
        try {
            SubscriptionUserResponse subscriptionUserResponse = subscriptionPackageUserService.savaPayment(subscriptionUserBean);
            return ResponseEntity.ok(new Response(0, "Payment saved successfully", subscriptionUserResponse));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null));
        }
    }



}
