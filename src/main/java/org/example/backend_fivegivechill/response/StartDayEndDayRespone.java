package org.example.backend_fivegivechill.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;


@AllArgsConstructor
@Data
@NoArgsConstructor
public class StartDayEndDayRespone {
    Date startDay;
    Date endDay;
}
