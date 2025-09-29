package org.example.backend_fivegivechill.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.backend_fivegivechill.entity.RavenueUserEntity;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RevenueUserBankRevenueUserResponse {
    private int id;
    private String bankAccountNumber;
    private String bankName;
    private String reason;
    private RevenueUserResponse revenueUserResponse;

}
