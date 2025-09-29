package org.example.backend_fivegivechill.controllers.Client;

import jakarta.servlet.http.HttpServletRequest;
import org.example.backend_fivegivechill.entity.UserEntity;
import org.example.backend_fivegivechill.entity.UserPinCodeEntity;
import org.example.backend_fivegivechill.repository.UserPinCodeRepository;
import org.example.backend_fivegivechill.repository.UserRepository;
import org.example.backend_fivegivechill.response.Response;
import org.example.backend_fivegivechill.utils.Encryption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;

@RestController
@RequestMapping("/user-pin-code")
@CrossOrigin("*")
public class UserPinCodeController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserPinCodeRepository userPinCodeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    Encryption encryption;

    @Value("${encryption}")
    private String encryptionKey;

    private UserEntity getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElse(null);
    }

    // 1. Kiểm tra người dùng đã thiết lập PIN chưa
    @GetMapping("/check")
    public ResponseEntity<Response> checkIfPinCodeExists() {
        try {
            UserEntity userEntity = getCurrentUser();
            UserPinCodeEntity userPinCodeEntity = userPinCodeRepository.findByUser(userEntity);

            if (userPinCodeEntity == null) {
                return ResponseEntity.ok(new Response(0, "Bạn chưa thiết lập mã PIN. Vui lòng tạo mã PIN để tiếp tục.", false));
            } else {
                return ResponseEntity.ok(new Response(0, "Bạn đã thiết lập mã PIN.", true));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, "Lỗi hệ thống khi kiểm tra mã PIN: " + e.getMessage(), null));
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<Response> verifyPinCode() {
        try {
            UserEntity userEntity = getCurrentUser();
            UserPinCodeEntity userPinCodeEntity = userPinCodeRepository.findByUser(userEntity);

            String pinCode = request.getHeader("pinCode");
            String pinStore = request.getHeader("pinStore");

            String dataToSign = pinCode + ":" + userEntity.getId();
            String signature = encryption.sign(dataToSign, encryptionKey);

            if(pinStore != null){
                boolean isMatch = passwordEncoder.matches(pinStore, userPinCodeEntity.getPinCode());
                if (isMatch) {
                    return ResponseEntity.ok(new Response(0, "Xác thực mã PIN thành công.", true));
                } else {
                    return ResponseEntity.ok(new Response(0, "Mã PIN không chính xác.", false));
                }
            }
            if (userPinCodeEntity == null) {
                return ResponseEntity.ok(new Response(0, "Bạn chưa thiết lập mã PIN. Vui lòng tạo mã PIN để tiếp tục.", false));
            }

            boolean isMatch = passwordEncoder.matches(signature, userPinCodeEntity.getPinCode());

            if (isMatch) {
                return ResponseEntity.ok(new Response(0, "Xác thực mã PIN thành công.", signature));
            } else {
                return ResponseEntity.ok(new Response(0, "Mã PIN không chính xác.", false));
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, "Lỗi khi xác thực mã PIN: " + e.getMessage(), null));
        }
    }

    // 3. Thiết lập mã PIN mới
    @PostMapping("/create")
    public ResponseEntity<Response> createPinCode() {
        try {
            String pinCode = request.getHeader("pinCode");
            System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa " + pinCode);
            UserEntity userEntity = getCurrentUser();
            UserPinCodeEntity existing = userPinCodeRepository.findByUser(userEntity);

            if (existing != null) {
                return ResponseEntity.ok(new Response(0, "Bạn đã thiết lập mã PIN trước đó.", false));
            }
            String dataToSign = pinCode + ":" + userEntity.getId();
            String signature = encryption.sign(dataToSign, encryptionKey);

            String encodedPin = passwordEncoder.encode(signature);

            UserPinCodeEntity userPinCode = new UserPinCodeEntity();
            userPinCode.setUser(userEntity);
            userPinCode.setPinCode(encodedPin);
            userPinCode.setCreateDate(new Date());
            userPinCodeRepository.save(userPinCode);

            return ResponseEntity.ok(new Response(0, "Thiết lập mã PIN thành công.", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, "Không thể tạo mã PIN: " + e.getMessage(), null));
        }
    }

}
