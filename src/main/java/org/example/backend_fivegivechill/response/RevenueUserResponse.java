package org.example.backend_fivegivechill.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RevenueUserResponse {
    private int id;
    private long amount;
    private Boolean type;
    private Date createDate;
    private int status;
    private String fullname;
}
