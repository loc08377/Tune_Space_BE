package org.example.backend_fivegivechill.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.example.backend_fivegivechill.beans.ChangePasswordBean;
import org.example.backend_fivegivechill.beans.RegisterBean;
import org.example.backend_fivegivechill.entity.SubscriptionUserEntity;
import org.example.backend_fivegivechill.entity.UserEntity;
import org.example.backend_fivegivechill.repository.SubscriptionUserRepository;
import org.example.backend_fivegivechill.repository.UserRepository;
import org.example.backend_fivegivechill.response.RegisterOtpResponse;
import org.example.backend_fivegivechill.response.Response;
import org.example.backend_fivegivechill.response.UserInfoRespose;
import org.example.backend_fivegivechill.services.AuthService;
import org.example.backend_fivegivechill.services.MailService;
import org.example.backend_fivegivechill.services.SubscriptionPackage_userService;
import org.example.backend_fivegivechill.utils.CryptoUtil;
import org.example.backend_fivegivechill.utils.Encryption;
import org.example.backend_fivegivechill.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@CrossOrigin("*")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MailService mailService;

    @Autowired
    private SubscriptionUserRepository subscriptionUserRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private Encryption encryption;

    @PostMapping("/login")
    public ResponseEntity<Response> login(@RequestParam String email, @RequestParam String password) {
        Response res = new Response();
        try {
            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password));

            UserEntity user = userRepository.findByEmail(email).orElse(null);

            String idEncrypt = null;
            if (user != null) {
                String idUser = String.valueOf(user.getId());
                idEncrypt = encryption.encrypt(idUser);
            }

            if (!user.isStatus()) {
                res.setData(null);
                res.setStatus(1);
                res.setMessage("Tài khoản đã bị khóa");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(res);
            }


            // Generate JWT token
            String role = authentication.getAuthorities().iterator().next().getAuthority();
            String token = jwtUtil.generateToken(email, role, idEncrypt);
    
            // Prepare response
            res.setData(token);
            res.setStatus(0);
            res.setMessage("Đăng nhập thành công");
            return ResponseEntity.ok().body(res);

        } catch (Exception e) {
            e.printStackTrace();
            res.setData(null);
            res.setStatus(1);
            res.setMessage("Email hoặc mật khẩu sai");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
        }
    }

    @GetMapping("/info")
    public ResponseEntity<Response> getUserInfo() {
        Response res = new Response();
        try {
            // Lấy thông tin xác thực từ SecurityContext
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                res.setStatus(1);
                res.setMessage("Người dùng chưa đăng nhập!");
                res.setData(null);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
            }

            String email = authentication.getName(); // Email được set làm principal trong token
            UserEntity user = userRepository.findByEmail(email).orElse(null);
            if (user == null) {
                res.setStatus(1);
                res.setMessage("Không tìm thấy người dùng!");
                res.setData(null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
            }

            res.setStatus(0);
            res.setMessage("Lấy thông tin thành công!");
            res.setData(new UserInfoRespose(user.getId(), user.getEmail(), user.getFullName(),user.getPhone(), user.getAvatar(),user.isStatus(), user.getRole()));
            return ResponseEntity.ok(res);

        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(1);
            res.setMessage("Có lỗi xảy ra khi lấy thông tin!");
            res.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    @PostMapping("/send-otp-register")
    public ResponseEntity<Response> sendOtpRegister(@RequestBody Map<String, String> body) {
        String email = body.get("email");

        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new Response(1, "Email không được để trống!", null));
        }

        try {
            RegisterOtpResponse res = authService.sendOtpRegister(email);

            return ResponseEntity.ok(
                    new Response(0, res.getMessage(), res) // dùng getMessage() nếu không dùng record
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new Response(1, "Lỗi khi gửi OTP!", null));
        }
    }


    @PostMapping("/register")
    public ResponseEntity<Response> register(@Valid @RequestBody RegisterBean registerBean,
                                             Errors errors) {

        if (errors.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            errors.getFieldErrors().forEach(f ->
                    errorMap.put(f.getField(), f.getDefaultMessage())
            );
            return ResponseEntity.badRequest()
                    .body(new Response(1, "Validation failed", errorMap));
        }

        try {
            UserEntity saved = authService.register(registerBean);
            return ResponseEntity.ok(
                    new Response(0, "Đăng ký thành công!", saved)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new Response(1, e.getMessage(), null));
        }
    }

    // Gửi OTP
    @PostMapping("/send-otp")
    public ResponseEntity<Response> sendOtp(@RequestBody Map<String, String> body) {
        String email = body.get("email");

        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new Response(1, "Email không được để trống!", null));
        }

        // Gọi service đã sửa (trả về response chứa otpToken & emailToken)
        Response serviceRes = authService.sendOtp(email);

        if (serviceRes.getStatus() == 0) {
            return ResponseEntity.ok(serviceRes);
        } else {
            return ResponseEntity.badRequest().body(serviceRes);
        }
    }

    // Xác minh OTP
    @PostMapping("/verify-otp")
    public ResponseEntity<Response> verifyOtp(@RequestBody Map<String, String> body) {
        String inputOtp = body.get("otp");
        String otpToken = body.get("otpToken");

        if (otpToken == null || otpToken.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new Response(1, "OTP đã hết hạn hoặc không tồn tại!", null));
        }

        boolean verified = authService.verifyOtp(inputOtp, otpToken);

        if (verified) {
            return ResponseEntity.ok(new Response(0, "OTP đúng", null));
        } else {
            return ResponseEntity.badRequest().body(new Response(1, "OTP sai hoặc lỗi xác minh!", null));
        }
    }

    // Đặt lại mật khẩu
    @PostMapping("/reset-password")
    public ResponseEntity<Response> resetPassword(@RequestBody Map<String, String> body) {
        String newPassword = body.get("newPassword");
        String emailToken  = body.get("emailToken");

        if (emailToken == null || emailToken.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new Response(1, "Email không tồn tại hoặc token đã hết hạn!", null));
        }

        try {
            UserEntity user = authService.resetPassword(newPassword, emailToken);
            return ResponseEntity.ok(new Response(0, "Đặt lại mật khẩu thành công!", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null));
        }
    }


    @PostMapping("/change-password")
    public ResponseEntity<Response> changePassword(@Valid @RequestBody ChangePasswordBean changePasswordBean,
                                                   Errors errors) {
        if (errors.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            errors.getFieldErrors().forEach(fieldError -> {
                errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
            });
            return ResponseEntity.badRequest().body(new Response(1, "Validation failed", errorMap));
        }

        try {
            UserEntity user = authService.changePassword(changePasswordBean);
            return ResponseEntity.ok(new Response(0, "Đổi mật khẩu thành công!", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(1, e.getMessage(), null));
        }
    }


}
