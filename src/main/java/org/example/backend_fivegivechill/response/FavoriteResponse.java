package org.example.backend_fivegivechill.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteResponse {
    private int status; // 0 = success, 1 = error
    private String message;
    private Object data;
}
