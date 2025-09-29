package org.example.backend_fivegivechill.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FollowBean {
    private int follower;     // ID người theo dõi
    private int userId;       // ID người được theo dõi
    private LocalDate createDate;
}

//11111