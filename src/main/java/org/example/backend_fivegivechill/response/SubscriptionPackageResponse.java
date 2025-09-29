package org.example.backend_fivegivechill.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionPackageResponse {
    private int id;
    private String name;
    private int duration;
    private int price;
    private boolean status;
}
