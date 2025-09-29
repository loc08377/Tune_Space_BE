package org.example.backend_fivegivechill.controllers.Client;

import jakarta.servlet.http.HttpServletRequest;
import org.example.backend_fivegivechill.entity.RavenueUserEntity;
import org.example.backend_fivegivechill.entity.RevenueUserBankEntity;
import org.example.backend_fivegivechill.entity.UserEntity;
import org.example.backend_fivegivechill.repository.RavenueUserRepository;
import org.example.backend_fivegivechill.repository.RevenueUserBankRepository;
import org.example.backend_fivegivechill.repository.UserRepository;
import org.example.backend_fivegivechill.response.*;
import org.example.backend_fivegivechill.services.RevanueUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequestMapping("/")
@CrossOrigin("*")
public class RevenueClientController {

    @Autowired
    RevanueUserService revanueUserService;

    @Autowired
    RavenueUserRepository ravenueUserRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RevenueUserBankRepository revenueUserBankRepository;


    @GetMapping("/user-ravenue/recalculator-user-ravenue")
    public ResponseEntity<Response> sdsd() {
        boolean recalculator = revanueUserService.recalculateMissingRevenuUser();
        if (recalculator) {
            return ResponseEntity.ok(new Response(0, "Cập nhật thành công!", recalculator));
        } else return ResponseEntity.badRequest().body(new Response(1, "Cập nhật thất bại!", recalculator));
    }

    @GetMapping("/user-ravenue/as")
    public ResponseEntity<Response> dsd() {
        revanueUserService.calculatetRevenueUser();
        return ResponseEntity.ok(new Response(0, "Cập nhật thành công!", true));
    }

    @GetMapping("/user-ravenue/checkEarnMoney")
    public ResponseEntity<Response> checkEarnMoney() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            UserEntity userOpt = userRepository.findByEmail(email).get();
            SongAndfollowQuantityRespone songAndfollowQuantityRespone = revanueUserService.checkEarnMoney(userOpt.getId());
            if (songAndfollowQuantityRespone.isEligibleToEarn()) {
                return ResponseEntity.ok(new Response(0, "đã bật kiếm tiền!", songAndfollowQuantityRespone));
            } else {
                return ResponseEntity.ok(new Response(0, "Chưa đủ điều kiện!", songAndfollowQuantityRespone));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(new Response(1, "loi!" + e, false));
        }

    }

    @PostMapping("/user-ravenue/withdraw-money/{drawMoney}")
    public ResponseEntity<Response> drawMoney(@PathVariable int drawMoney, @RequestBody BankRespone bankRespone) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            Optional<UserEntity> userOpt = userRepository.findByEmail(email);
            if (drawMoney < 10000) {
                return ResponseEntity.ok(new Response(0, "Số tiền rút phải tối thiểu 10.000 VND", false));
            }
            String message = revanueUserService.withdrawMoney(drawMoney, userOpt.get().getId(), bankRespone);
            return ResponseEntity.ok(new Response(0, message, true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, "Giao dịch thất bại. Hãy thử lai!" + e.getMessage(), null));

        }
    }

    @PostMapping("/user-ravenue/revenue-day-to-day")
    public ResponseEntity<Response> revenue(@RequestBody StartDayEndDayRespone startDayEndDayRespone) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            Optional<UserEntity> userOpt = userRepository.findByEmail(email);
            // tinh cua hom nay ne
            RavenueUserEntity ravenueUserEntity = revanueUserService.calculateTodayUserRevenueFromMemory(userOpt.get().getId());
            // tinh tien tu truoc den hom qua vi hom nay ham tinh tien chua co chay nen su dung o trne
            Long totolAmountAllTimeExceptToDay = revanueUserService.calculatorRavenueUser(userOpt.get().getId(), startDayEndDayRespone.getStartDay(), startDayEndDayRespone.getEndDay());
            Long result = totolAmountAllTimeExceptToDay + ravenueUserEntity.getAmount();
            long total = (result != null) ? result : 0L;
            return ResponseEntity.ok(new Response(0, "số tiền ở cái ngày m nhập dô là : " + total + "m biết chưa có nhiêu mà m coi quài", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, "Giao dịch thất bại. Hãy thử lại! " + e.getMessage(), 0));
        }
    }

    @GetMapping("/revenue-amount")
    public ResponseEntity<Response> a() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            UserEntity userEntity = userRepository.findByEmail(email).get();
            Long result = ravenueUserRepository.getTotalAmountDifferenceByUser(userEntity.getId());
            return ResponseEntity.ok(new Response(0, "lây thành cong r", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, "Giao dịch thất bại. Hãy thử lai!" + e.getMessage(), null));

        }
    }

    @GetMapping("/revenue-amount/pendding")
    public ResponseEntity<Response> asssss() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            UserEntity userEntity = userRepository.findByEmail(email).get();
            Long result = ravenueUserRepository.getTotalAmountByDateRangeAndTypeTrueAndStatus0(userEntity.getId());
            return ResponseEntity.ok(new Response(0, "lây thành cong r", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, "Giao dịch thất bại. Hãy thử lai!" + e.getMessage(), null));

        }
    }

    @GetMapping("/revenue-amount/pending")
    public ResponseEntity<Response> pending() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            UserEntity userEntity = userRepository.findByEmail(email).get();
            Long result = ravenueUserRepository.getTotalAmountDifferenceByUser(userEntity.getId());
            return ResponseEntity.ok(new Response(0, "lây thành cong r", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, "Giao dịch thất bại. Hãy thử lai!" + e.getMessage(), null));

        }
    }

    @PostMapping("/revenue-user/cancel")
    public ResponseEntity<Response> cancel(@RequestBody RevenueUserBankRevenueUserResponse revenueUserResponse) {
        try {
            RavenueUserEntity ravenueUserEntity = ravenueUserRepository.findById(revenueUserResponse.getId()).get();
            RevenueUserBankEntity revenueUserBankEntity =
                    revenueUserBankRepository.findByRevenueUserId(ravenueUserEntity.getId());
            if (ravenueUserEntity == null) {
                return ResponseEntity.ok(new Response(0, "Giao dichj không tồn tại", false));
            }
            if (ravenueUserEntity.getStatus() == 1 || ravenueUserEntity.getStatus() == 2) {
                return ResponseEntity.ok(new Response(0, "Giao dịch này đã được sử lí trước đó", false));
            }
            ravenueUserRepository.save(new RavenueUserEntity(
                    ravenueUserEntity.getId(),
                    ravenueUserEntity.getUserEntity(),
                    ravenueUserEntity.getAmount(),
                    ravenueUserEntity.getType(),
                    ravenueUserEntity.getCreateDate(),
                    3
            ));

            revenueUserBankRepository.save(new RevenueUserBankEntity(
                    revenueUserBankEntity.getId(),
                    revenueUserBankEntity.getRevenueUser(),
                    revenueUserBankEntity.getBankAccountNumber(),
                    revenueUserBankEntity.getBankName(),
                    revenueUserResponse.getReason()
            ));

            return ResponseEntity.ok(new Response(0, "Đã hủy giao dịch thành công!", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, "vui lòng thử lại", null));

        }
    }

    @PostMapping("/revenue-user/confirm-pay/success")
    public ResponseEntity<Response> confirmPayment(
            @RequestBody RevenueUserBankRevenueUserResponse revenueUserBankRevenueUserResponse) {
        try {
            RevenueUserBankEntity revenueUserBankEntity =
                    revenueUserBankRepository.findById(revenueUserBankRevenueUserResponse.getId()).orElse(null);

            if (revenueUserBankEntity == null) {
                return ResponseEntity.ok(new Response(0, "Giao dịch không tồn tại", false));
            }

            if (revenueUserBankEntity.getRevenueUser().getStatus() != 0) {
                return ResponseEntity.ok(new Response(0, "Giao dịch này đã được xử lý trước đó", false));
            }

            // Update status to confirmed (1)
            ravenueUserRepository.save(new RavenueUserEntity(
                    revenueUserBankEntity.getRevenueUser().getId(),
                    revenueUserBankEntity.getRevenueUser().getUserEntity(),
                    revenueUserBankEntity.getRevenueUser().getAmount(),
                    revenueUserBankEntity.getRevenueUser().getType(),
                    revenueUserBankEntity.getRevenueUser().getCreateDate(),
                    1
            ));

            revenueUserBankRepository.save(new RevenueUserBankEntity(
                    revenueUserBankEntity.getId(),
                    revenueUserBankEntity.getRevenueUser(),
                    revenueUserBankEntity.getBankAccountNumber(),
                    revenueUserBankEntity.getBankName(),
                    revenueUserBankRevenueUserResponse.getReason()
            ));

            return ResponseEntity.ok(new Response(0, "Đã xác nhận giao dịch thành công!", true));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, "Vui lòng thử lại", null));
        }
    }

    @PostMapping("/revenue-user/confirm-pay/refuse")
    public ResponseEntity<Response> refusePayment(
            @RequestBody RevenueUserBankRevenueUserResponse revenueUserBankRevenueUserResponse) {
        try {
            RevenueUserBankEntity revenueUserBankEntity =
                    revenueUserBankRepository.findById(revenueUserBankRevenueUserResponse.getId()).orElse(null);

            if (revenueUserBankEntity == null) {
                return ResponseEntity.ok(new Response(0, "Giao dịch không tồn tại", false));
            }

            if (revenueUserBankEntity.getRevenueUser().getStatus() != 0) {
                return ResponseEntity.ok(new Response(0, "Giao dịch này đã được xử lý trước đó", false));
            }

            // Update status to refused (2)
            ravenueUserRepository.save(new RavenueUserEntity(
                    revenueUserBankEntity.getRevenueUser().getId(),
                    revenueUserBankEntity.getRevenueUser().getUserEntity(),
                    revenueUserBankEntity.getRevenueUser().getAmount(),
                    revenueUserBankEntity.getRevenueUser().getType(),
                    revenueUserBankEntity.getRevenueUser().getCreateDate(),
                    2
            ));

            revenueUserBankRepository.save(new RevenueUserBankEntity(
                    revenueUserBankEntity.getId(),
                    revenueUserBankEntity.getRevenueUser(),
                    revenueUserBankEntity.getBankAccountNumber(),
                    revenueUserBankEntity.getBankName(),
                    revenueUserBankRevenueUserResponse.getReason()
            ));

            return ResponseEntity.ok(new Response(0, "Đã từ chối giao dịch thành công!", true));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, "Vui lòng thử lại", null));
        }
    }
}
