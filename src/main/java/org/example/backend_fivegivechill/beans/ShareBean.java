package org.example.backend_fivegivechill.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShareBean {

    //lop moi
    private int userId;
    private String sharingMethod;
    private String recipient;
    private Integer songId;
}
