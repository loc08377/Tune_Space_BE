package org.example.backend_fivegivechill.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {
    private int id;
    private int songId;
    private String songName;
    private String songAvatar;
    private boolean songVip;
    private int status;
    private String contentReport;
}
