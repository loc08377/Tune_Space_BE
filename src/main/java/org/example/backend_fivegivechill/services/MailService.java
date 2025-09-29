package org.example.backend_fivegivechill.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Xác nhận OTP - FiveGiveChill");
            message.setText("Mã OTP của bạn là: " + otp + "\nVui lòng không chia sẻ mã này với bất kỳ ai");

            mailSender.send(message);
            System.out.println("Đã gửi OTP thành công đến " + toEmail);
        } catch (Exception e) {
            System.err.println("Lỗi khi gửi email: " + e.getMessage());
        }
    }
}
