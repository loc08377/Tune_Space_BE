package org.example.backend_fivegivechill.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportMapRespone {
    private Integer reportId;
    private String content;
    private Date createDate;
    private Integer userId;
    private String userName;
    private Integer songId;
    private String songName;
}
