package org.example.backend_fivegivechill.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class DeleteHistoryResponse {
    private List<Integer> historyIds;

    public List<Integer> getHistoryIds() {
        return historyIds;
    }


}
