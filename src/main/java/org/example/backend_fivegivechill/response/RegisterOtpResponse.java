package org.example.backend_fivegivechill.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegisterOtpResponse {
    private int status;
    private String message;
    private String otp;
}
