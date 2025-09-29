package org.example.backend_fivegivechill.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterBean {
    private String avatar;
    private String email;
    private String password;
    private String fullName;
    private String phone;
    private String otp;
    private String otpToken;

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
