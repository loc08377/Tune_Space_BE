package org.example.backend_fivegivechill.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.backend_fivegivechill.entity.SongEntity;

import java.util.Date;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyRavenueRespone {
    private int id;

    private List<SongResponese> songs;
    private long totalAmount;
    private int countList;
    private Date createDate;

}
