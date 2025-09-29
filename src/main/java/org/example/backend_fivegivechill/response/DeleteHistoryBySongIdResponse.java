package org.example.backend_fivegivechill.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteHistoryBySongIdResponse {
    private List<Integer> songId;

    public List<Integer> getSongIds() {
        return songId;
    }
}
