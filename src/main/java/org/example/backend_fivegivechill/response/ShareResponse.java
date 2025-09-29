package org.example.backend_fivegivechill.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShareResponse {
    private int shareId;
    private int userId;
    private String sharingMethod;
    private String recipient;
    private Date createDate;


    //lop moi
    private SongResponese song;
}