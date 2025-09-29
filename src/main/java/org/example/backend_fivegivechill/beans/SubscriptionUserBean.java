package org.example.backend_fivegivechill.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionUserBean {
    private int price;
    private int sub_id; // ID của SubscriptionPackageEntity
    private int user_id; // ID của UserEntity
}