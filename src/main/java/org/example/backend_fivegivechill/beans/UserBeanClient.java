package org.example.backend_fivegivechill.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserBeanClient {
    private int id;
    private boolean status;
    private String fullName;
    private String email;
    private String phone;
    private String avatar;
}
