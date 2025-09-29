package org.example.backend_fivegivechill.controllers.Client;

import org.example.backend_fivegivechill.entity.RavenueUserEntity;
import org.example.backend_fivegivechill.entity.RevenueUserBankEntity;
import org.example.backend_fivegivechill.entity.SongEntity;
import org.example.backend_fivegivechill.entity.SubscriptionPackageEntity;
import org.example.backend_fivegivechill.repository.RavenueUserRepository;
import org.example.backend_fivegivechill.repository.RevenueUserBankRepository;
import org.example.backend_fivegivechill.repository.UserRepository;
import org.example.backend_fivegivechill.response.Response;
import org.example.backend_fivegivechill.response.SongResponese;
import org.example.backend_fivegivechill.services.PaymentService;
import org.example.backend_fivegivechill.services.RevanueUserService;
import org.example.backend_fivegivechill.services.SubscriptionPackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@CrossOrigin("*")
public class PaymentaController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private SubscriptionPackageService subscriptionPackageService;

    @Autowired
    RevanueUserService revanueUserService;

    @Autowired
    RavenueUserRepository ravenueUserRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RevenueUserBankRepository revenueUserBankRepository;

    @GetMapping("/payment/subscription/{id}")
    public ResponseEntity<Response> paymentSubscription(@PathVariable int id) {
        try {
            SubscriptionPackageEntity subscriptionPackageEntity = subscriptionPackageService.getPackageById(id);
            if (subscriptionPackageEntity != null) {
                String urlPayment = paymentService.getPay(subscriptionPackageEntity);
                return ResponseEntity.ok(new Response(0, "get url payment success", urlPayment));
            }
            return ResponseEntity.badRequest().body(new Response(1, "get subscription not found", null));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null));
        }
    }

//    @GetMapping("/payment/confirm-pay/{selectedTransactionId}")
//    public ResponseEntity<Response> paymentSubscsription(@PathVariable int selectedTransactionId) {
//        try {
//            RevenueUserBankEntity revenueUserBankEntity = revenueUserBankRepository.findById(selectedTransactionId).get();
//
//            RavenueUserEntity ravenueUserEntity = ravenueUserRepository.findById(revenueUserBankEntity.getRevenueUser().getId()).get();
//            ravenueUserEntity.setType(true);
//            ravenueUserEntity.setStatus(1);
//            ravenueUserRepository.save(ravenueUserEntity);
//
//            return ResponseEntity.ok(new Response(0, "success", null));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null));
//        }
//    }
}
