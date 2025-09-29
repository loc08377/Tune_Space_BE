package org.example.backend_fivegivechill.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminRespone {
    private double statisticsByMonth;
    private double statisticsByYear;
    private int statisticsSumUsers;
    private int statisticsSumCreator;
    private int statisticsSumSongs;
    private List<Object[]> statisticsTop5Songs;
    private List<Object[]> statisticsLineChart;
    private List<Object[]> listSubPackUser;
}
