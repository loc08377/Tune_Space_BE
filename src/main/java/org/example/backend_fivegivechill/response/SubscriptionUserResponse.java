package org.example.backend_fivegivechill.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionUserResponse {

    private int id;
    private int price;
    private String title_subs;
    private Date createDate;
    private Date firstDay;
    private Date lastDay;
    private boolean status;
    private int sub_id;

    private int duration;


}
