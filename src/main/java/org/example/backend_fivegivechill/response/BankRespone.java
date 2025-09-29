package org.example.backend_fivegivechill.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.backend_fivegivechill.entity.UserEntity;

import java.time.LocalDate;
import java.util.List;


@AllArgsConstructor
@Data
@NoArgsConstructor
public class BankRespone {
    private int id;
    private String numberAccount;
    private String nameAccount;
    private LocalDate createDate;
    private List<Integer> ids;
}
