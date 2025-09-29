package org.example.backend_fivegivechill.controllers.Client;

import lombok.RequiredArgsConstructor;
import org.example.backend_fivegivechill.entity.BankEntity;
import org.example.backend_fivegivechill.entity.UserEntity;
import org.example.backend_fivegivechill.repository.BankRepository;
import org.example.backend_fivegivechill.repository.UserRepository;
import org.example.backend_fivegivechill.response.BankRespone;
import org.example.backend_fivegivechill.response.Response;
import org.example.backend_fivegivechill.services.BankSevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@CrossOrigin("*")
@RequestMapping("/")
public class BankController {


    @Autowired
    BankSevice bankSevice;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BankRepository bankRepository;

    @PostMapping("/bank")
    public ResponseEntity<Response> revenue(@RequestBody BankRespone bankRespone) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            Optional<UserEntity> userOpt = userRepository.findByEmail(email);
            String message = bankSevice.CreateBank(userOpt.get().getId(), bankRespone);

            return ResponseEntity.ok(new Response(0, message, message));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, "Giao dịch thất bại. Hãy thử lại! " + e.getMessage(), 0));
        }
    }



    @GetMapping("/bank/bank-card")
    public ResponseEntity<Response> bankCardByUser() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            UserEntity user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user với email: " + email));

            List<BankEntity> banks = bankRepository.findByUserId(user.getId());

            List<BankRespone> data = banks.stream()
                    .map(b -> new BankRespone(
                            b.getId(),
                            b.getNumberAccount(),
                            b.getNameAccount(),
                            b.getCreateDate(),
                            null
                    ))
                    .toList();

            return ResponseEntity.ok(new Response(0, "thành công", data));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new Response(1, "Giao dịch thất bại. Hãy thử lại! " + e.getMessage(), 0));
        }
    }

    @PostMapping("/bank/update-status")
    public ResponseEntity<Response> update(@RequestBody BankRespone bankRespone) {
        try {
            List<Integer> ids = bankRespone.getIds();
            System.out.println("IDs nhận được từ request: " + ids);

            if (ids == null || ids.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new Response(1, "Bạn chưa chọn thông tin nào để xóa", null));
            }

            bankSevice.updateStatus(ids);

            return ResponseEntity.ok(new Response(0, "Xóa thông tin ngân hàng thành công", null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(new Response(1, "Xóa thông tin ngân hàng không thành công", null));
        }
    }

}
