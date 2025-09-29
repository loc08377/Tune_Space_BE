package org.example.backend_fivegivechill.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.backend_fivegivechill.entity.IdCardEntity;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class IdCardResponse {
    private int id;
    private String numberId;
    private Float similarity;
    private IdCardEntity.Status status;
    private LocalDateTime createDate;
}
