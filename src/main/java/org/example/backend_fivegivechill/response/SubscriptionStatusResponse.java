package org.example.backend_fivegivechill.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionStatusResponse {
    private boolean isVip; // true nếu người dùng là VIP, false nếu không
    private String daysRemaining; // Số ngày còn lại
}
