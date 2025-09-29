package org.example.backend_fivegivechill.services;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.example.backend_fivegivechill.beans.ChangePasswordBean;
import org.example.backend_fivegivechill.beans.RegisterBean;
import org.example.backend_fivegivechill.entity.UserEntity;
import org.example.backend_fivegivechill.repository.UserRepository;
import org.example.backend_fivegivechill.response.RegisterOtpResponse;
import org.example.backend_fivegivechill.response.Response;
import org.example.backend_fivegivechill.utils.CryptoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private MailService mailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    private String generateOtp() {
        int otp = (int) (Math.random() * 900000) + 100000; //tạo random 6 số cho otp
        return String.valueOf(otp);
    }

    public RegisterOtpResponse sendOtpRegister(String email) throws Exception {
        String otp = generateOtp();

        // Gửi mail
        mailService.sendOtpEmail(email, otp);
        System.out.printf("[SEND OTP] email=%s, otp=%s%n", email, otp);

        // Mã hóa OTP thành token
        String token = CryptoUtil.encrypt(otp);

        // Trả về cho front‑end (status = 0 => thành công)
        return new RegisterOtpResponse(0, "Gửi OTP thành công", token);
    }


    public UserEntity register(RegisterBean bean) throws Exception {
        String inputOtp  = bean.getOtp();       // người dùng gõ
        String otpToken  = bean.getOtpToken();  // front‑end gửi kèm

        if (otpToken == null)
            throw new Exception("OTP đã hết hạn hoặc không tồn tại!");

        String decryptedOtp = CryptoUtil.decrypt(otpToken);
        if (decryptedOtp == null || !decryptedOtp.equals(inputOtp))
            throw new Exception("OTP không đúng!");

        if (userRepository.findByEmail(bean.getEmail()).isPresent())
            throw new Exception("Email đã được sử dụng!");

        UserEntity user = new UserEntity();
        user.setAvatar(bean.getAvatar());
        user.setPhone(bean.getPhone());
        user.setFullName(bean.getFullName());
        user.setEmail(bean.getEmail());
        user.setPassword(passwordEncoder.encode(bean.getPassword()));
        user.setRole(1);
        user.setStatus(true);

        return userRepository.save(user);
    }

    // Gửi OTP khi người dùng quên mật khẩu
    public Response sendOtp(String email) {
        try {
            Optional<UserEntity> optionalUser = userRepository.findByEmail(email);
            if (optionalUser.isEmpty()) {
                return new Response(1, "Email không tồn tại trong hệ thống!", null);
            }

            // Tạo và gửi OTP
            String otp = generateOtp();
            String sendTo = email.contains("gmail.com") ? email : "firejack947@gmail.com";
            mailService.sendOtpEmail(sendTo, otp);

            // Mã hoá OTP và email
            String otpToken = CryptoUtil.encrypt(otp);
            String emailToken = CryptoUtil.encrypt(email);

            // Trả về cho front-end để tự lưu vào cookie
            return new Response(0, "Gửi OTP thành công!",
                    Map.of("otpToken", otpToken, "emailToken", emailToken)
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(1, "Lỗi khi gửi OTP!", null);
        }
    }


    public boolean verifyOtp(String inputOtp, String otpToken) {
        try {
            String decryptedOtp = CryptoUtil.decrypt(otpToken);
            return inputOtp != null && inputOtp.equals(decryptedOtp);
        } catch (Exception e) {
            return false;
        }
    }


    public UserEntity resetPassword(String newPassword, String emailToken) throws Exception {
        String decryptedEmail = CryptoUtil.decrypt(emailToken);

        Optional<UserEntity> optionalUser = userRepository.findByEmail(decryptedEmail);
        if (optionalUser.isEmpty()) {
            throw new Exception("Email không tồn tại!");
        }

        UserEntity user = optionalUser.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }


    public UserEntity changePassword(ChangePasswordBean changePasswordBean) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new Exception("Bạn chưa đăng nhập!");
        }

        String email = authentication.getName();
        Optional<UserEntity> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new Exception("Không tìm thấy người dùng!");
        }

        UserEntity user = optionalUser.get();

        if (!passwordEncoder.matches(changePasswordBean.getOldPassword(), user.getPassword())) {
            throw new Exception("Mật khẩu cũ không đúng!");
        }

        user.setPassword(passwordEncoder.encode(changePasswordBean.getNewPassword()));
        return userRepository.save(user);
    }


}
