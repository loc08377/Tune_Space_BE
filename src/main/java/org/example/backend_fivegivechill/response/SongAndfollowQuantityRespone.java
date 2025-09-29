package org.example.backend_fivegivechill.response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SongAndfollowQuantityRespone {
    int followQuantity;
    int recentSongCount;
    int topListenCount;
    boolean EligibleToEarn;
}
