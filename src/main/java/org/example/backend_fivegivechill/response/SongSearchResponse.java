package org.example.backend_fivegivechill.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SongSearchResponse {
    private Integer id;
    private String name;
    private String avatar;
    private Boolean vipSong;
    private String categoryNames;
    private String artistNames;
    private String artistName;
    private String userName;
}
