package org.example.backend_fivegivechill.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.example.backend_fivegivechill.entity.RavenueUserEntity;
import org.example.backend_fivegivechill.entity.RevenueUserBankEntity;
import org.example.backend_fivegivechill.repository.RavenueUserRepository;
import org.example.backend_fivegivechill.repository.RevenueUserBankRepository;
import org.example.backend_fivegivechill.repository.UserRepository;
import org.example.backend_fivegivechill.response.Response;
import org.example.backend_fivegivechill.response.RevenueUserBankRevenueUserResponse;
import org.example.backend_fivegivechill.services.RevanueUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@CrossOrigin("*")
public class RevenueController {

//    @Autowired
//    RevanueUserService revanueUserService;
//
//    @Autowired
//    RavenueUserRepository ravenueUserRepository;
//
//    @Autowired
//    UserRepository userRepository;
//
//    @Autowired
//    RevenueUserBankRepository revenueUserBankRepository;
//
//    @PostMapping({"/revenue-user/comfirm-pay/success", "/revenue-user/comfirm-pay/refuse"})
//    public ResponseEntity<Response> cancel(
//            @RequestBody RevenueUserBankRevenueUserResponse revenueUserBankRevenueUserResponse,
//            HttpServletRequest request
//    ) {
//        try {
//            RevenueUserBankEntity revenueUserBankEntity =
//                    revenueUserBankRepository.findById(revenueUserBankRevenueUserResponse.getId()).orElse(null);
//
//            if (revenueUserBankEntity == null) {
//                return ResponseEntity.ok(new Response(0, "Giao dịch không tồn tại", false));
//            }
//
//            if (revenueUserBankEntity.getRevenueUser().getStatus() != 0) {
//                return ResponseEntity.ok(new Response(0, "Giao dịch này đã được xử lý trước đó", false));
//            }
//
//            // Lấy URI để kiểm tra là xác nhận hay từ chối
//            String uri = request.getRequestURI();
//            int newStatus = uri.endsWith("/refuse") ? 2 : 1;
//
//            // Lưu cập nhật trạng thái mới
//            ravenueUserRepository.save(new RavenueUserEntity(
//                    revenueUserBankEntity.getRevenueUser().getId(),
//                    revenueUserBankEntity.getRevenueUser().getUserEntity(),
//                    revenueUserBankEntity.getRevenueUser().getAmount(),
//                    revenueUserBankEntity.getRevenueUser().getType(),
//                    revenueUserBankEntity.getRevenueUser().getCreateDate(),
//                    newStatus
//            ));
//
//            revenueUserBankRepository.save(new RevenueUserBankEntity(
//                    revenueUserBankEntity.getId(),
//                    revenueUserBankEntity.getRevenueUser(),
//                    revenueUserBankEntity.getBankAccountNumber(),
//                    revenueUserBankEntity.getBankName(),
//                    revenueUserBankRevenueUserResponse.getReason()
//            ));
//
//            String message = newStatus == 2 ? "Đã từ chối giao dịch thành công!" : "Đã xác nhận giao dịch thành công!";
//            return ResponseEntity.ok(new Response(0, message, true));
//
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(new Response(1, "Vui lòng thử lại", null));
//        }
//    }
}
