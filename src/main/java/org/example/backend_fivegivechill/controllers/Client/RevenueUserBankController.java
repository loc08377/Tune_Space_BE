package org.example.backend_fivegivechill.controllers.Client;

import org.example.backend_fivegivechill.entity.RavenueUserEntity;
import org.example.backend_fivegivechill.entity.RevenueUserBankEntity;
import org.example.backend_fivegivechill.entity.UserEntity;
import org.example.backend_fivegivechill.repository.RevenueUserBankRepository;
import org.example.backend_fivegivechill.repository.UserRepository;
import org.example.backend_fivegivechill.response.BankRespone;
import org.example.backend_fivegivechill.response.Response;
import org.example.backend_fivegivechill.response.RevenueUserBankRevenueUserResponse;
import org.example.backend_fivegivechill.response.RevenueUserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/revenue-user-bank")
@CrossOrigin("*")
public class RevenueUserBankController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RevenueUserBankRepository revenueUserBankRepository;

    @GetMapping("/history-bank")
    public ResponseEntity<Response> historyBank() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            UserEntity userEntity = userRepository.findByEmail(email).get();

            List<RevenueUserBankEntity> revenueUserBankEntities;
            if(userEntity.getRole() == 0){
                revenueUserBankEntities = revenueUserBankRepository.findByTypedraw();
            }else {
                revenueUserBankEntities = revenueUserBankRepository.findByUserAndTypedraw(userEntity);
            }

            List<RevenueUserBankRevenueUserResponse> revenueUserBankRevenueUserResponses = revenueUserBankEntities.stream().map(
                    revenueUserBankEntity -> new RevenueUserBankRevenueUserResponse(
                            revenueUserBankEntity.getId(),
                            revenueUserBankEntity.getBankAccountNumber(),
                            revenueUserBankEntity.getBankName(),
                            revenueUserBankEntity.getReason(),

                            new RevenueUserResponse(
                                    revenueUserBankEntity.getRevenueUser().getId(),
                                    revenueUserBankEntity.getRevenueUser().getAmount(),
                                    revenueUserBankEntity.getRevenueUser().getType(),
                                    revenueUserBankEntity.getRevenueUser().getCreateDate(),
                                    revenueUserBankEntity.getRevenueUser().getStatus(),
                                    revenueUserBankEntity.getRevenueUser().getUserEntity().getFullName()
                            )
                    )
            ).collect(Collectors.toList());
            return ResponseEntity.ok(new Response(0, "lay danh sách thành công", revenueUserBankRevenueUserResponses));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, "Giao dịch thất bại. Hãy thử lại! " + e.getMessage(), 0));
        }
    }

    @GetMapping("/history-bank-income")
    public ResponseEntity<Response> historyBankIncome() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            UserEntity userEntity = userRepository.findByEmail(email).get();

            List<RavenueUserEntity> ravenueUserEntities = revenueUserBankRepository.findByUserfindByUserAndTypePlus(userEntity);

            List<RevenueUserResponse> revenueUserBankRevenueUserResponses = ravenueUserEntities.stream().map(
                    ravenueUserEntity -> new RevenueUserResponse(
                            ravenueUserEntity.getId(),
                            ravenueUserEntity.getAmount(),
                            ravenueUserEntity.getType(),
                            ravenueUserEntity.getCreateDate(),
                            ravenueUserEntity.getStatus(),
                            ravenueUserEntity.getUserEntity().getFullName()
                    )
            ).collect(Collectors.toList());
            return ResponseEntity.ok(new Response(0, "lay danh sách thành công", revenueUserBankRevenueUserResponses));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, "Giao dịch thất bại. Hãy thử lại! " + e.getMessage(), 0));
        }
    }
}
